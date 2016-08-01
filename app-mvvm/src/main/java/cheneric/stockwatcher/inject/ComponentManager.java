package cheneric.stockwatcher.inject;

import android.app.Application;

import cheneric.stockwatcher.inject.dagger2.BaseComponentManager;

public class ComponentManager extends BaseComponentManager {

	public ComponentManager(final Application application) {
		createComponent(ApplicationComponent.class, () -> { return createApplicationComponent(application); });
	}

	ApplicationComponent createApplicationComponent(Application application) {
		return DaggerApplicationComponent.builder()
			.module(new ApplicationComponent.Module(application))
			.build();
	}

	public ApplicationComponent getApplicationComponent() {
		return getComponent(ApplicationComponent.class);
	}

	StockQuoteDetailComponent createStockDetailComponent() {
		return DaggerStockQuoteDetailComponent.builder()
			.applicationComponent(getApplicationComponent())
			.build();
	}

	public StockQuoteDetailComponent getOrCreateStockDetailComponent() {
		return getOrCreateComponent(StockQuoteDetailComponent.class, this::createStockDetailComponent);
	}

	StockQuoteListComponent createStockListComponent() {
		return DaggerStockQuoteListComponent.builder()
			.applicationComponent(getApplicationComponent())
			.build();
	}

	public StockQuoteListComponent getOrCreateStockListComponent() {
		return getOrCreateComponent(StockQuoteListComponent.class, this::createStockListComponent);
	}
}
