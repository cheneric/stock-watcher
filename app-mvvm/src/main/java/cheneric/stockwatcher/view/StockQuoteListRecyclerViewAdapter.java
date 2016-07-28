package cheneric.stockwatcher.view;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.google.auto.factory.AutoFactory;
import com.google.auto.factory.Provided;

import javax.inject.Inject;

import cheneric.stockwatcher.StockWatcherApplication;
import cheneric.stockwatcher.databinding.StockQuoteListItemBinding;
import cheneric.stockwatcher.model.StockSymbolList;
import cheneric.stockwatcher.view.StockQuoteListFragment.ItemSelectedListener;
import cheneric.stockwatcher.viewmodel.StockQuoteListItemViewModel;
import cheneric.stockwatcher.viewmodel.StockQuoteListItemViewModelFactory;

/**
 * {@link StockSymbolList} {@link RecyclerView.Adapter} that passes user interactions to
 * {@link StockQuoteListFragment.ItemSelectedListener}.
 */
@AutoFactory
public class StockQuoteListRecyclerViewAdapter extends RecyclerView.Adapter<StockQuoteListRecyclerViewAdapter.ViewHolder> {

	private final ItemSelectedListener itemSelectedListener;
	private final StockSymbolList stockSymbolList;

	StockQuoteListRecyclerViewAdapter(@Provided StockSymbolList stockSymbolList, StockQuoteListFragment.ItemSelectedListener itemSelectedListener) {
		this.stockSymbolList = stockSymbolList;
		this.itemSelectedListener = itemSelectedListener;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		final StockQuoteListItemBinding binding =
			StockQuoteListItemBinding.inflate(
				LayoutInflater.from(
					parent.getContext()),
					parent,
					false);
		return new ViewHolder(binding);
	}

	@Override
	public void onBindViewHolder(final ViewHolder viewHolder, int position) {
		viewHolder.bind(position, stockSymbolList.get(position), itemSelectedListener);
	}

	@Override
	public int getItemCount() {
		return stockSymbolList.size();
	}

	public static class ViewHolder extends RecyclerView.ViewHolder {
		private final StockQuoteListItemBinding binding;

		@Inject
		StockQuoteListItemViewModelFactory viewModelFactory;

		public ViewHolder(StockQuoteListItemBinding binding) {
			super(binding.listItem);
			this.binding = binding;
			// inject
			((StockWatcherApplication)binding.listItem
				.getContext()
					.getApplicationContext())
						.getComponentManager()
							.getOrCreateStockListComponent()
								.inject(this);
		}

		void bind(int itemIndex, String symbol, StockQuoteListFragment.ItemSelectedListener itemSelectedListener) {
			final StockQuoteListItemViewModel viewModel = binding.getViewModel();
			if (viewModel == null) {
				binding.setViewModel(viewModelFactory.create(itemIndex, symbol, itemSelectedListener));
			}
			else {
				viewModel.update(itemIndex, symbol);
			}
		}
	}
}
