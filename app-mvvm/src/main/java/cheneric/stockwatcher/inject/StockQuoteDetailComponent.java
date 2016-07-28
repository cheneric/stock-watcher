package cheneric.stockwatcher.inject;

import cheneric.stockwatcher.inject.scope.ViewScope;
import cheneric.stockwatcher.view.StockQuoteDetailActivity;
import cheneric.stockwatcher.view.StockQuoteDetailFragment;

@ViewScope(StockQuoteDetailFragment.class)
@dagger.Component(modules=StockQuoteDetailComponent.Module.class, dependencies=ApplicationComponent.class)
public interface StockQuoteDetailComponent extends ApplicationComponent {

	void inject(StockQuoteDetailActivity stockQuoteDetailActivity);
	void inject(StockQuoteDetailFragment stockQuoteDetailFragment);

	@dagger.Module
	static class Module {}
}
