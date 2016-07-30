package cheneric.stockwatcher.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.auto.factory.AutoFactory;
import com.pluscubed.recyclerfastscroll.RecyclerFastScroller;

import javax.inject.Inject;

import cheneric.stockwatcher.R;
import cheneric.stockwatcher.StockWatcherApplication;
import cheneric.stockwatcher.databinding.StockQuoteListFragmentBinding;
import cheneric.stockwatcher.inject.ComponentManager;
import cheneric.stockwatcher.inject.StockQuoteListComponent;
import cheneric.stockwatcher.view.widget.DividerItemDecoration;
import cheneric.stockwatcher.viewmodel.StockQuoteListViewModel;
import lombok.NoArgsConstructor;
import timber.log.Timber;

/**
 * A fragment representing a list of Items.
 * <p>
 * Activities containing this fragment MUST implement the {@link ItemSelectedListener}
 * interface.
 */
@AutoFactory
@NoArgsConstructor
public class StockQuoteListFragment extends Fragment {
	private static final String COLUMN_COUNT_ARG_KEY = "COLUMN_COUNT";
	private static final int SCROLL_TO_TOP_OFFSET_PIXELS = 100;

	private int columnCount = 1;
	private ComponentManager componentManager;
	private ItemSelectedListener itemSelectedListener;
	private RecyclerView.LayoutManager layoutManager;
	private RecyclerView recyclerView;

	@Inject
	StockQuoteListRecyclerViewAdapterFactory stockQuoteListRecyclerViewAdapterFactory;

	@SuppressLint("ValidFragment")
	public StockQuoteListFragment(int columnCount) {
		final Bundle args = new Bundle();
		args.putInt(COLUMN_COUNT_ARG_KEY, columnCount);
		setArguments(args);
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		if (context instanceof ItemSelectedListener) {
			itemSelectedListener = (ItemSelectedListener)context;
		}
		else {
			throw new RuntimeException(context + " must implement " + ItemSelectedListener.class);
		}
		// inject
		componentManager =
			((StockWatcherApplication)context.getApplicationContext())
				.getComponentManager();
		componentManager.getOrCreateStockListComponent()
			.inject(this);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final Bundle arguments = getArguments();
		if (arguments != null) {
			columnCount = arguments.getInt(COLUMN_COUNT_ARG_KEY);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final StockQuoteListFragmentBinding binding = StockQuoteListFragmentBinding.inflate(inflater);
		binding.setViewModel(new StockQuoteListViewModel(this));
		final View rootView = binding.getRoot();
		final RecyclerView recyclerView = this.recyclerView = (RecyclerView)rootView.findViewById(R.id.list);
		((RecyclerFastScroller)rootView.findViewById(R.id.fast_scroller)).attachRecyclerView(recyclerView);
		final Context context = recyclerView.getContext();
		if (columnCount <= 1) {
			layoutManager = new LinearLayoutManager(context);
			recyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL_LIST));
		}
		else {
			layoutManager = new GridLayoutManager(context, columnCount);
		}
		recyclerView.setLayoutManager(layoutManager);
		recyclerView.setAdapter(stockQuoteListRecyclerViewAdapterFactory.create(itemSelectedListener));
		return rootView;
	}

	public void smoothScrollToPosition(int position) {
		Timber.d("stock quote list smooth scrolling to index: %s", position);
		recyclerView.smoothScrollToPosition(position);
	}

	public void scrollToPosition(int position) {
		Timber.d("stock quote list scrolling to index: %s", position);
		if (layoutManager instanceof LinearLayoutManager) {
			((LinearLayoutManager)layoutManager).scrollToPositionWithOffset(position, SCROLL_TO_TOP_OFFSET_PIXELS);
		}
		else {
			layoutManager.scrollToPosition(position);
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		componentManager.destroyComponent(StockQuoteListComponent.class);
		itemSelectedListener = null;
	}

	/**
	 * Must be implemented by activities containing this fragment.
	 */
	public interface ItemSelectedListener {
		void onItemSelected(int listIndex);
	}
}
