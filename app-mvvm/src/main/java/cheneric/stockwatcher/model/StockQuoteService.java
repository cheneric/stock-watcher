package cheneric.stockwatcher.model;

import android.text.TextUtils;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import cheneric.stockwatcher.inject.scope.ApplicationScope;
import cheneric.stockwatcher.util.LinkedSet;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;
import timber.log.Timber;

/**
 * Network service to fetch {@link StockQuote}s.
 */
@ApplicationScope
public class StockQuoteService extends Observable<StockQuote> {
	private final LinkedSet<String> pendingQuoteSymbols;

	@Inject
	StockQuoteService(OnSubscribe onSubscribe) {
		super(onSubscribe);
		this.pendingQuoteSymbols = onSubscribe.pendingQuoteSymbols;
	}

	void fetchStockQuote(String symbol) {
		Timber.d("queuing fetch stock quote: %s", symbol);
		pendingQuoteSymbols.add(symbol);
	}

	@ApplicationScope
	static class OnSubscribe implements Observable.OnSubscribe<StockQuote> {
		private final long MAX_BATCH_MILLIS = 50;
		private final int MAX_BATCH_SIZE = 20;
		private final Subject<StockQuote,StockQuote> stockQuotePublishSubject = PublishSubject.create();
		final LinkedSet<String> pendingQuoteSymbols = new LinkedSet<>();

		@Inject
		RawStockQuoteService rawStockQuoteService;

		@Inject
		OnSubscribe() {
			pendingQuoteSymbols.getObservable()
				.subscribeOn(Schedulers.io())
				.buffer(MAX_BATCH_MILLIS, TimeUnit.MILLISECONDS, MAX_BATCH_SIZE)
				.filter(symbols -> symbols.size() > 0)
				.map(this::getStockQuotes)
				.concatMap(stockQuotesList -> Observable.from(stockQuotesList))
				.subscribe(stockQuotePublishSubject);
		}

		@Override
		public void call(Subscriber<? super StockQuote> subscriber) {
			stockQuotePublishSubject.subscribe(subscriber);
		}

		List<StockQuote> getStockQuotes(List<String> symbols) {
			try {
				// workaround for Yahoo service returning a single result as an object instead of a list
				if (symbols.size() == 1) {
					symbols.add("GOOGL".equals(symbols.get(0)) ? "GOOG" : "GOOGL");
				}

				Timber.d("fetching %s stock quotes", symbols.size());
				Timber.v("fetching symbols: %s", symbols);
				final String query =
					"SELECT Symbol, Name, LastTradePriceOnly, ChangeinPercent FROM yahoo.finance.quotes WHERE symbol IN (\""
						+ TextUtils.join("\", \"", symbols)
						+ "\")";
				final Response<StockQuoteResult> stockQuotesResponse =
					rawStockQuoteService.getStockQuotes(query)
						.execute();
				final List<StockQuote> stockQuotes =
					stockQuotesResponse.body()
						.getQuery()
						.getResult()
						.getStockQuotes();
				Timber.d("received %s stock quotes", stockQuotes.size());
				return stockQuotes;
			}
			catch (IOException exception) {
				throw new RuntimeException(exception);
			}
		}
	}

	public interface RawStockQuoteService {
		@GET("v1/public/yql?format=json&env=store%3a%2f%2fdatatables.org%2falltableswithkeys")
		Call<StockQuoteResult> getStockQuotes(@Query("q") String query);

		class Factory {
			public RawStockQuoteService create() {
//				HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
//				interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
//				OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

				return new Retrofit.Builder()
					.baseUrl("https://query.yahooapis.com/")
//					.client(client)
					.addConverterFactory(GsonConverterFactory.create())
					.build()
					.create(RawStockQuoteService.class);
			}
		}
	}
}