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
import cheneric.stockwatcher.inject.ComponentManager;
import cheneric.stockwatcher.inject.StockQuoteListComponent;
import cheneric.stockwatcher.view.util.Lifecycle;
import cheneric.stockwatcher.viewmodel.StockQuoteDetailViewModelFactory;
import lombok.NoArgsConstructor;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;
import timber.log.Timber;

/**
 * Fragment displaying {@link cheneric.stockwatcher.model.StockQuote} details.
 */
@AutoFactory
@NoArgsConstructor
public class StockQuoteDetailFragment extends Fragment {
	private static final String ITEM_INDEX_ARG_KEY = "ITEM_INDEX";
	private static final String STOCK_LIST_SIZE_ARG_KEY = "STOCK_LIST_SIZE";
	private static final String SYMBOL_ARG_KEY = "SYMBOL";

	private final Subject<Lifecycle,Lifecycle> lifecycleSubject = PublishSubject.create();
	private ComponentManager componentManager;
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
		componentManager =
			((StockWatcherApplication)context.getApplicationContext())
				.getComponentManager();
		componentManager.getOrCreateStockDetailComponent()
			.inject(this);
		// args
		final Bundle bundle = getArguments();
		itemIndex = bundle.getInt(ITEM_INDEX_ARG_KEY, -1);
		stockListSize = bundle.getInt(STOCK_LIST_SIZE_ARG_KEY, -1);
		symbol = bundle.getString(SYMBOL_ARG_KEY);
	}

	@Override
	public void onStart() {
		final Lifecycle lifecycle = Lifecycle.start;
		Timber.d("lifecycle: %s", lifecycle);
		super.onStart();
		lifecycleSubject.onNext(lifecycle);
	}

	@Override
	public void onStop() {
		final Lifecycle lifecycle = Lifecycle.stop;
		Timber.d("lifecycle: %s", lifecycle);
		super.onStop();
		lifecycleSubject.onNext(lifecycle);
	}

	@Override
	public void onDetach() {
		final Lifecycle lifecycle = Lifecycle.detach;
		Timber.d("lifecycle: %s", lifecycle);
		super.onDetach();
		lifecycleSubject.onNext(lifecycle);
		componentManager.destroyComponent(StockQuoteListComponent.class);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final StockQuoteDetailFragmentBinding binding = StockQuoteDetailFragmentBinding.inflate(inflater);
		final View rootView = binding.getRoot();
		binding.setViewModel(stockQuoteDetailViewModelFactory.create(itemIndex, stockListSize, symbol, rootView, lifecycleSubject));
		return rootView;
	}
}
