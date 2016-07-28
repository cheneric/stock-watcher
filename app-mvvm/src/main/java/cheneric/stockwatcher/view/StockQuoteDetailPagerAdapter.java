package cheneric.stockwatcher.view;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.google.auto.factory.AutoFactory;
import com.google.auto.factory.Provided;

import cheneric.stockwatcher.model.StockSymbolList;

@AutoFactory
public class StockQuoteDetailPagerAdapter extends FragmentStatePagerAdapter {

	private final StockSymbolList stockSymbolList;
	private final StockQuoteDetailFragmentFactory stockQuoteDetailFragmentFactory;

	public StockQuoteDetailPagerAdapter(@Provided StockSymbolList stockSymbolList, @Provided StockQuoteDetailFragmentFactory stockQuoteDetailFragmentFactory, FragmentManager fragmentManager) {
		super(fragmentManager);
		this.stockSymbolList = stockSymbolList;
		this.stockQuoteDetailFragmentFactory = stockQuoteDetailFragmentFactory;
	}

	@Override
	public Fragment getItem(int position) {
		return stockQuoteDetailFragmentFactory.create(
			position,
			stockSymbolList.size(),
			stockSymbolList.get(position));
	}

	@Override
	public int getCount() { return stockSymbolList.size();}
}
