package cheneric.stockwatcher.viewmodel;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.view.View;

import com.google.auto.factory.AutoFactory;
import com.google.auto.factory.Provided;

import cheneric.stockwatcher.BR;
import cheneric.stockwatcher.model.Preferences;
import cheneric.stockwatcher.model.StockQuote;
import cheneric.stockwatcher.model.StockQuoteProvider;
import cheneric.stockwatcher.view.util.Lifecycle;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

@AutoFactory
public class StockQuoteDetailViewModel extends BaseObservable {
	private static final long REFRESH_MILLIS = 2000;

	// observed
	private String changePercent;
	private Boolean isPriceGain;
	private String name;
	private String price;

	// unobserved
	private final Preferences preferences;
	private final StockQuoteProvider stockQuoteProvider;
	private final CompositeSubscription subscriptions = new CompositeSubscription();

	private final int itemIndex;
	private boolean isAutoRefreshEnabled = true;
	private Subscription lifecycleSubscription;
	private final View rootView;
	private final int stockListSize;
	private final String symbol;

	public StockQuoteDetailViewModel(@Provided StockQuoteProvider stockQuoteProvider, @Provided Preferences preferences, int itemIndex, int stockListSize, String symbol, View rootView, Observable<Lifecycle> lifecycleObservable) {
		this.stockQuoteProvider = stockQuoteProvider;
		this.preferences = preferences;
		this.itemIndex = itemIndex;
		this.stockListSize = stockListSize;
		this.symbol = symbol;
		this.rootView = rootView;
		subscriptions.add(
			stockQuoteProvider.getStockQuote(symbol)
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(stockQuote -> update(stockQuote)));
		subscriptions.add(
			lifecycleObservable.subscribeOn(AndroidSchedulers.mainThread())
				.subscribe(lifecycle -> {
					switch(lifecycle) {
						case start:
							enableAutoRefresh();
							startAutoRefresh();
							break;
						case stop:
							disableAutoRefresh();
							break;
						case detach:
							Timber.v("ondetach unbsubscribing subscriptions: %s", symbol);
							subscriptions.unsubscribe();
							break;
					}
				}));
	}

	@Bindable
	public String getChangePercent() { return changePercent; }

	public void setChangePercent(String changePercent) {
		this.changePercent = changePercent;
		notifyPropertyChanged(BR.changePercent);
	}

	@Bindable
	public Boolean getIsPriceGain() { return isPriceGain; }

	public void setIsPriceGain(Boolean isPriceGain) {
		this.isPriceGain = isPriceGain;
		notifyPropertyChanged(BR.isPriceGain);
	}

	@Bindable
	public String getName() { return name; }

	public void setName(String name) {
		this.name = name;
		notifyPropertyChanged(BR.name);
	}

	@Bindable
	public String getPrice() { return price; }

	public void setPrice(String price) {
		this.price = price;
		notifyPropertyChanged(BR.price);
	}

	public int getItemIndex() { return itemIndex; }

	public int getStockListSize() { return stockListSize; }

	public String getSymbol() {
		return symbol;
	}

	void update(StockQuote stockQuote) {
		setName(stockQuote.getName());
		setPrice(stockQuote.getPrice());
		setChangePercent(stockQuote.getChangePercent());
		setIsPriceGain(stockQuote.isPriceGain());
		startAutoRefresh();
	}

	void disableAutoRefresh() {
		Timber.d("disabling auto refresh: %s", symbol);
		isAutoRefreshEnabled = false;
	}

	void enableAutoRefresh() {
		Timber.d("enabling auto refresh: %s", symbol);
		isAutoRefreshEnabled = true;
	}

	void startAutoRefresh() {
		if (preferences.isAutoRefreshEnabled()) {
			if (isAutoRefreshEnabled) {
				rootView.postDelayed(
					() -> stockQuoteProvider.updateStockQuote(symbol),
					REFRESH_MILLIS);
			}
		}
		else {
			Timber.d("auto refresh disabled by preferences");
		}
	}
}
