package cheneric.stockwatcher.inject;

import cheneric.stockwatcher.inject.scope.ViewScope;
import cheneric.stockwatcher.view.StockQuoteListFragment;
import cheneric.stockwatcher.view.StockQuoteListRecyclerViewAdapter;
import cheneric.stockwatcher.viewmodel.StockQuoteListViewModel;

@ViewScope(StockQuoteListFragment.class)
@dagger.Component(modules=StockQuoteListComponent.Module.class, dependencies=ApplicationComponent.class)
public interface StockQuoteListComponent extends ApplicationComponent {

	void inject(StockQuoteListFragment stockQuoteListFragment);
	void inject(StockQuoteListRecyclerViewAdapter.ViewHolder viewHolder);
	void inject(StockQuoteListViewModel stockQuoteListViewModel);

	@dagger.Module
	static class Module {}
}
