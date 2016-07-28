package cheneric.stockwatcher.model;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import cheneric.stockwatcher.inject.scope.ApplicationScope;
import rx.Observable;
import rx.Observer;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;
import timber.log.Timber;

@ApplicationScope
public class StockQuoteProvider implements Observer<StockQuote> {
	static final long MAX_STOCK_QUOTE_AGE_MILLIS = 5 * 60 * 1000; // 5 minutes
	// avoid synchronization by performing all reads/writes on main thread
	private final Map<String,StockQuote> stockQuoteCache = new HashMap<>();
	private final Subject<StockQuote,StockQuote> stockQuotesPublishSubject = PublishSubject.create();
	private final StockQuoteService stockQuoteService;

	@Inject
	StockQuoteProvider(StockQuoteService stockQuoteService) {
		this.stockQuoteService = stockQuoteService;
		// subscribe to stock quote service results
		stockQuoteService.subscribeOn(Schedulers.io())
			.subscribe(this);
	}

	public Observable<StockQuote> getStockQuote(String symbol) {
		final StockQuote cachedStockQuote = getCachedStockQuote(symbol);
		Observable<StockQuote> stockQuoteObservable = createObservable(symbol);
		if (cachedStockQuote == null) {
			stockQuoteService.fetchStockQuote(symbol);
		}
		else {
			stockQuoteObservable = stockQuoteObservable.mergeWith(Observable.just(cachedStockQuote));
		}
		return stockQuoteObservable;
	}

	Observable<StockQuote> createObservable(String symbol) {
		return stockQuotesPublishSubject
			.filter(stockQuote -> symbol.equals(stockQuote.getSymbol()));
	}

	final StockQuote getCachedStockQuote(String symbol) {
		final StockQuote cachedStockQuote = stockQuoteCache.get(symbol);
		final StockQuote returnStockQuote =
			cachedStockQuote == null || !isFresh(cachedStockQuote) ?
				null :
				cachedStockQuote;
		Timber.v("cached stock quote = %s", returnStockQuote);
		return returnStockQuote;
	}

	final boolean isFresh(StockQuote stockQuote) {
		return System.currentTimeMillis() - stockQuote.getTimestamp() < MAX_STOCK_QUOTE_AGE_MILLIS;
	}

	// Observer

	@Override
	public void onCompleted() {}

	@Override
	public void onError(Throwable throwable) {
		Timber.e(throwable, "error fetching stock quote");
	}

	@Override
	public void onNext(StockQuote stockQuote) {
		Timber.v("caching: %s", stockQuote);
		stockQuoteCache.put(stockQuote.getSymbol(), stockQuote);
		stockQuotesPublishSubject.onNext(stockQuote);
	}
}
