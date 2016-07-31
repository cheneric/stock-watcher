package cheneric.stockwatcher.viewmodel;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.view.View;

import com.google.auto.factory.AutoFactory;
import com.google.auto.factory.Provided;

import cheneric.stockwatcher.BR;
import cheneric.stockwatcher.model.StockQuote;
import cheneric.stockwatcher.model.StockQuoteProvider;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@AutoFactory
public class StockQuoteDetailViewModel extends BaseObservable {
	private static final long REFRESH_MILLIS = 2000;

	// observed
	private String changePercent;
	private Boolean isPriceGain;
	private String name;
	private String price;

	// unobserved
	private final int itemIndex;
	private final int stockListSize;
	private final String symbol;
	private final View rootView;

	private final StockQuoteProvider stockQuoteProvider;

	public StockQuoteDetailViewModel(@Provided StockQuoteProvider stockQuoteProvider, int itemIndex, int stockListSize, String symbol, View rootView) {
		this.stockQuoteProvider = stockQuoteProvider;
		this.itemIndex = itemIndex;
		this.stockListSize = stockListSize;
		this.symbol = symbol;
		this.rootView = rootView;
		stockQuoteProvider.getStockQuote(symbol)
			.subscribeOn(Schedulers.io())
			.observeOn(AndroidSchedulers.mainThread())
			.subscribe(stockQuote -> update(stockQuote));
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
		autoRefresh();
	}

	void autoRefresh() {
		rootView.postDelayed(
			() -> stockQuoteProvider.updateStockQuote(symbol),
			REFRESH_MILLIS);
	}
}
