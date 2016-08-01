package cheneric.stockwatcher.viewmodel;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.view.View;

import com.google.auto.factory.AutoFactory;
import com.google.auto.factory.Provided;

import cheneric.stockwatcher.BR;
import cheneric.stockwatcher.model.StockQuote;
import cheneric.stockwatcher.model.StockQuoteProvider;
import cheneric.stockwatcher.view.StockQuoteListFragment;
import cheneric.stockwatcher.view.util.Lifecycle;
import lombok.Setter;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

@AutoFactory
public class StockQuoteListItemViewModel extends BaseObservable {
	private static final long REFRESH_MILLIS = 2000;

	// observed
	private Boolean isPriceGain;
	private String price;
	private String symbol;

	// unobserved
	private final StockQuoteListFragment.ItemSelectedListener itemSelectedListener;
	private final StockQuoteProvider stockQuoteProvider;

	@Setter
	private int itemIndex = -1;

	private boolean isAutoRefreshEnabled = true;
	private View rootView;
	private Subscription lifecycleSubscription;
	private Subscription stockQuoteSubscription;


	public StockQuoteListItemViewModel(@Provided StockQuoteProvider stockQuoteProvider, int itemIndex, String symbol, View rootView, Observable<Lifecycle> lifecycleObservable, StockQuoteListFragment.ItemSelectedListener itemSelectedListener) {
		this.stockQuoteProvider = stockQuoteProvider;
		this.rootView = rootView;
		this.itemSelectedListener = itemSelectedListener;
		update(itemIndex, symbol);
		lifecycleSubscription =
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
							if (stockQuoteSubscription != null) {
								Timber.v("on detach unsubscribing (%s): symbol %s, subscription %s", itemIndex, symbol, stockQuoteSubscription);
								stockQuoteSubscription.unsubscribe();
							}
							Timber.v("on detach unsubscribing (%s): symbol %s, subscription %s", itemIndex, symbol, lifecycleSubscription);
							lifecycleSubscription.unsubscribe();
							break;
					}
				});
	}

	@Bindable
	public Boolean getIsPriceGain() { return isPriceGain; }

	public void setIsPriceGain(Boolean isPriceGain) {
		this.isPriceGain= isPriceGain;
		notifyPropertyChanged(BR.isPriceGain);
	}

	public int getItemIndex() {
		return itemIndex;
	}

	@Bindable
	public String getPrice() { return price; }

	public void setPrice(String price) {
		this.price = price;
		notifyPropertyChanged(BR.price);
	}

	@Bindable
	public String getSymbol() { return symbol; }

	public void setSymbol(String symbol) {
		this.symbol = symbol;
		notifyPropertyChanged(BR.symbol);
	}

	public void recycle() {
		update(null);
		final Subscription subscription = this.stockQuoteSubscription;
		if (subscription != null) {
			Timber.v("on recycle unsubscribing (%s): symbol %s, subscription %s", itemIndex, symbol, lifecycleSubscription);
			subscription.unsubscribe();
		}
	}

	public void update(int itemIndex, String symbol) {
		setItemIndex(itemIndex);
		setSymbol(symbol);
		this.stockQuoteSubscription = stockQuoteProvider.getStockQuote(symbol)
			.subscribeOn(Schedulers.io())
			.subscribe(new Observer<StockQuote>() {
				@Override
				public void onCompleted() {}

				@Override
				public void onError(Throwable throwable) {
					Timber.e(throwable, "error updating stock quote list item (%s): %s", itemIndex, symbol);
				}

				@Override
				public void onNext(StockQuote stockQuote) { update(stockQuote); }
			});
	}

	void update(StockQuote stockQuote) {
		if (stockQuote == null) {
			Timber.d("recycling stock list item (%s): %s", itemIndex, symbol);
		}
		else {
			Timber.d("updating stock list item (%s): %s", itemIndex, stockQuote);
		}
		setPrice(stockQuote == null ? null : stockQuote.getPrice());
		setIsPriceGain(stockQuote == null ? null : stockQuote.isPriceGain());
		startAutoRefresh();
	}

	void disableAutoRefresh() {
		Timber.d("disabling auto refresh (%s): %s", itemIndex, symbol);
		isAutoRefreshEnabled = false;
	}

	void enableAutoRefresh() {
		Timber.d("enabling auto refresh (%s): %s", itemIndex, symbol);
		isAutoRefreshEnabled = true;
	}

	void startAutoRefresh() {
		if (isAutoRefreshEnabled && symbol != null) {
			rootView.postDelayed(() -> {
					final String symbol = this.symbol;
					if (symbol != null) {
						stockQuoteProvider.updateStockQuote(symbol);
					}
				},
				REFRESH_MILLIS);
		}
	}

	public void onItemSelected(View view) {
		Timber.d("clicked on stock index: %s, symbol: %s", itemIndex, symbol);
		if (itemSelectedListener != null) {
			itemSelectedListener.onItemSelected(itemIndex);
		}
	}
}

