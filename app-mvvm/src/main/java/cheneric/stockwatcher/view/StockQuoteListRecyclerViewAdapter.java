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
import cheneric.stockwatcher.view.util.Lifecycle;
import cheneric.stockwatcher.viewmodel.StockQuoteListItemViewModel;
import cheneric.stockwatcher.viewmodel.StockQuoteListItemViewModelFactory;
import rx.Observable;

/**
 * {@link StockSymbolList} {@link RecyclerView.Adapter} that passes user interactions to
 * {@link StockQuoteListFragment.ItemSelectedListener}.
 */
@AutoFactory
public class StockQuoteListRecyclerViewAdapter extends RecyclerView.Adapter<StockQuoteListRecyclerViewAdapter.ViewHolder> {

	private final ItemSelectedListener itemSelectedListener;
	private final Observable<Lifecycle> lifecycleObservable;
	private final StockSymbolList stockSymbolList;

	StockQuoteListRecyclerViewAdapter(@Provided StockSymbolList stockSymbolList, Observable<Lifecycle> lifecycleObservable, StockQuoteListFragment.ItemSelectedListener itemSelectedListener) {
		this.stockSymbolList = stockSymbolList;
		this.lifecycleObservable = lifecycleObservable;
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
		return new ViewHolder(binding, lifecycleObservable);
	}

	@Override
	public void onBindViewHolder(final ViewHolder viewHolder, int position) {
		viewHolder.bind(position, stockSymbolList.get(position), itemSelectedListener);
	}

	@Override
	public void onViewRecycled(ViewHolder viewHolder) {
		super.onViewRecycled(viewHolder);
		viewHolder.recycle();
	}

	@Override
	public int getItemCount() {
		return stockSymbolList.size();
	}

	public static class ViewHolder extends RecyclerView.ViewHolder {
		private final StockQuoteListItemBinding binding;
		private final Observable<Lifecycle> lifecycleObservable;

		@Inject
		StockQuoteListItemViewModelFactory viewModelFactory;

		public ViewHolder(StockQuoteListItemBinding binding, Observable<Lifecycle> lifecycleObservable) {
			super(binding.getRoot());
			this.binding = binding;
			this.lifecycleObservable = lifecycleObservable;
			// inject
			((StockWatcherApplication)binding.getRoot()
				.getContext()
					.getApplicationContext())
						.getComponentManager()
							.getOrCreateStockListComponent()
								.inject(this);
		}

		void bind(int itemIndex, String symbol, StockQuoteListFragment.ItemSelectedListener itemSelectedListener) {
			final StockQuoteListItemViewModel viewModel = binding.getViewModel();
			if (viewModel == null) {
				binding.setViewModel(viewModelFactory.create(itemIndex, symbol, binding.getRoot(), lifecycleObservable, itemSelectedListener));
			}
			else {
				viewModel.update(itemIndex, symbol);
			}
		}

		void recycle() {
			binding.getViewModel()
				.recycle();
		}
	}
}
