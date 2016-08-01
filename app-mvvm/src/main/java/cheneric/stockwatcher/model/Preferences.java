package cheneric.stockwatcher.model;

import android.net.ConnectivityManager;

import javax.inject.Inject;

import cheneric.stockwatcher.inject.scope.ApplicationScope;

@ApplicationScope
public class Preferences {
	@Inject
	ConnectivityManager connectivityManager;

	@Inject
	Preferences() {}

	public boolean isAutoRefreshEnabled() {
		return connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
			.isConnected();
	}
}
