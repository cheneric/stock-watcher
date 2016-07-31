package cheneric.stockwatcher.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.auto.factory.AutoFactory;

import javax.inject.Inject;

import cheneric.stockwatcher.StockWatcherApplication;
import cheneric.stockwatcher.databinding.StockQuoteDetailFragmentBinding;
import cheneric.stockwatcher.viewmodel.StockQuoteDetailViewModelFactory;
import lombok.NoArgsConstructor;

/**
 * Fragment displaying {@link cheneric.stockwatcher.model.StockQuote} details.
 */
@AutoFactory
@NoArgsConstructor
public class StockQuoteDetailFragment extends Fragment {
	private final String ITEM_INDEX_ARG_KEY = "ITEM_INDEX";
	private final String STOCK_LIST_SIZE_ARG_KEY = "STOCK_LIST_SIZE";
	private final String SYMBOL_ARG_KEY = "SYMBOL";

	private int itemIndex;
	private int stockListSize;
	private String symbol;

	@Inject
	StockQuoteDetailViewModelFactory stockQuoteDetailViewModelFactory;

	@SuppressLint("ValidFragment")
	StockQuoteDetailFragment(int itemIndex, int stockListSize, String symbol) {
		final Bundle args = new Bundle();
		args.putInt(ITEM_INDEX_ARG_KEY, itemIndex);
		args.putInt(STOCK_LIST_SIZE_ARG_KEY, stockListSize);
		args.putString(SYMBOL_ARG_KEY, symbol);
		setArguments(args);
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		// inject
		((StockWatcherApplication)context.getApplicationContext())
			.getComponentManager()
				.getOrCreateStockDetailComponent()
					.inject(this);
		// args
		final Bundle bundle = getArguments();
		itemIndex = bundle.getInt(ITEM_INDEX_ARG_KEY, -1);
		stockListSize = bundle.getInt(STOCK_LIST_SIZE_ARG_KEY, -1);
		symbol = bundle.getString(SYMBOL_ARG_KEY);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final StockQuoteDetailFragmentBinding binding = StockQuoteDetailFragmentBinding.inflate(inflater);
		final View rootView = binding.getRoot();
		binding.setViewModel(stockQuoteDetailViewModelFactory.create(itemIndex, stockListSize, symbol, rootView));
		return rootView;
	}
}
