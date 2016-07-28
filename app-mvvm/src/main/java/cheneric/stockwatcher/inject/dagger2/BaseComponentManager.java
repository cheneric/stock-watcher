package cheneric.stockwatcher.inject.dagger2;

import java.util.HashMap;
import java.util.Map;

import rx.functions.Func0;
import timber.log.Timber;

public class BaseComponentManager {
	private final Map<Class<?>,Component> components = new HashMap<>();

	protected <C extends Component> C createComponent(Class<C> componentInterface, Func0<C> createFunction) {
		Timber.i("creating component: %s", componentInterface);
		final C component = createFunction.call();
		components.put(componentInterface, component);
		return component;
	}

	public void destroyComponent(Class<? extends Component> componentInterface) {
		final Component component = components.remove(componentInterface);
		if (component == null) {
			Timber.w("no component to remove: %s", componentInterface);
		}
		else {
			Timber.i("destroyed component: %s", componentInterface);
		}
	}

	protected <C extends Component> C getOrCreateComponent(Class<C> componentInterface, Func0<C> createFunction) {
		C component = getComponent(componentInterface);
		if (component == null) {
			component = createComponent(componentInterface, createFunction);
		}
		else {
			Timber.d("returning existing component: %s", componentInterface);
		}
		return component;
	}

	protected <C extends Component> C getComponent(Class<C> componentInterface) {
		return (C)components.get(componentInterface);
	}
}
