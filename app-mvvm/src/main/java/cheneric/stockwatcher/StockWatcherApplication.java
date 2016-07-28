package cheneric.stockwatcher;

import android.app.Application;

import cheneric.stockwatcher.inject.ComponentManager;
import lombok.Getter;
import timber.log.Timber;

public class StockWatcherApplication extends Application {
	@Getter
	private ComponentManager componentManager;

	@Override
	public void onCreate() {
		super.onCreate();
		Timber.plant(new Timber.DebugTree());
		componentManager = new ComponentManager(this);
		componentManager.getApplicationComponent()
			.inject(this);
	}
}
