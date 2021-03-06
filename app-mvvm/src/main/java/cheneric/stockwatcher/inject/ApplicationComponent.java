package cheneric.stockwatcher.inject;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;

import java.io.IOException;

import cheneric.stockwatcher.StockWatcherApplication;
import cheneric.stockwatcher.inject.dagger2.Component;
import cheneric.stockwatcher.inject.scope.ApplicationScope;
import cheneric.stockwatcher.model.Preferences;
import cheneric.stockwatcher.model.StockQuoteProvider;
import cheneric.stockwatcher.model.StockQuoteService.RawStockQuoteService;
import cheneric.stockwatcher.model.StockSymbolList;
import dagger.Provides;
import timber.log.Timber;

@ApplicationScope
@dagger.Component(modules=ApplicationComponent.Module.class)
public interface ApplicationComponent extends Component {

	void inject(StockWatcherApplication application);

	Preferences preferences();
	StockQuoteProvider stockQuoteProvider();
	StockSymbolList stockSymbolList();

	@dagger.Module
	static class Module {
		private final Context context;

		Module(Application application) {
			this.context = application;
		}

		@Provides
		@ApplicationScope
		StockSymbolList provideStockSymbolList() {
			final StockSymbolList stockSymbolList = new StockSymbolList();
			try {
				stockSymbolList.load();
			}
			catch (IOException exception) {
				Timber.w(exception, "error loading stock symbol list");
			}
			return stockSymbolList;
		}

		@Provides
		@ApplicationScope
		RawStockQuoteService provideRawStockQuoteService() {
			return new RawStockQuoteService.Factory()
				.create();
		}

		@Provides
		ConnectivityManager provideConnectivityManager() {
			return (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		}
	}
}
