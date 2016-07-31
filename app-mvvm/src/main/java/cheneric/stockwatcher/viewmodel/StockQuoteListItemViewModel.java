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
import lombok.Setter;
import rx.Observer;
import rx.Subscription;
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
	@Setter
	private int itemIndex = -1;
	private View rootView;
	private Subscription subscription;

	private final StockQuoteProvider stockQuoteProvider;
	private final StockQuoteListFragment.ItemSelectedListener itemSelectedListener;

	public StockQuoteListItemViewModel(@Provided StockQuoteProvider stockQuoteProvider, int itemIndex, String symbol, View rootView, StockQuoteListFragment.ItemSelectedListener itemSelectedListener) {
		this.stockQuoteProvider = stockQuoteProvider;
		this.rootView = rootView;
		this.itemSelectedListener = itemSelectedListener;
		update(itemIndex, symbol);
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
		final Subscription subscription = this.subscription;
		if (subscription != null) {
			Timber.v("unsubscribing: %s", subscription);
			subscription.unsubscribe();
		}
	}

	public void update(int itemIndex, String symbol) {
		setItemIndex(itemIndex);
		setSymbol(symbol);
		this.subscription = stockQuoteProvider.getStockQuote(symbol)
			.subscribeOn(Schedulers.io())
			.subscribe(new Observer<StockQuote>() {
				@Override
				public void onCompleted() {}

				@Override
				public void onError(Throwable throwable) {
					Timber.e(throwable, "error updating stock quote list detail item");
				}

				@Override
				public void onNext(StockQuote stockQuote) { update(stockQuote); }
			});
	}

	void update(StockQuote stockQuote) {
		if (stockQuote == null) {
			Timber.d("recycling stock detail list item (%s): %s", itemIndex, symbol);
		}
		else {
			Timber.d("updating stock detail list item (%s): %s", itemIndex, stockQuote);
		}
		setPrice(stockQuote == null ? null : stockQuote.getPrice());
		setIsPriceGain(stockQuote == null ? null : stockQuote.isPriceGain());
		autoRefresh();
	}

	void autoRefresh() {
		rootView.postDelayed(() -> {
			final String symbol = this.symbol;
			if (rootView.isShown() && symbol != null) {
				stockQuoteProvider.updateStockQuote(symbol);
			}
		},
		REFRESH_MILLIS);
	}

	public void onItemSelected(View view) {
		Timber.d("clicked on stock index: %s, symbol: %s", itemIndex, symbol);
		if (itemSelectedListener != null) {
			itemSelectedListener.onItemSelected(itemIndex);
		}
	}
}

