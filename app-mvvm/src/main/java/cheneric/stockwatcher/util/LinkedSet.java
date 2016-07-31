package cheneric.stockwatcher.util;

import org.apache.commons.collections4.map.LinkedMap;

import java.util.Collection;

import lombok.Getter;
import lombok.ToString;
import rx.Observable;
import rx.Observer;
import rx.observables.SyncOnSubscribe;

@ToString(of = "map")
public class LinkedSet<T> {
	private final LinkedMap<T,T> map = new LinkedMap<>();
	private final Object lock = map;

	@Getter
	private final Observable<T> observable = createObservable();

	/**
	 * Adds the element.
	 *
	 * @param element the element to add.
	 * @return <code>true</code> if the element was not already in the set; otherwise, returns
	 * <code>false</code>.
	 */
	public boolean add(T element) {
		boolean isNew;
		synchronized(lock) {
			isNew = rawAdd(element);
			lock.notifyAll();
		}
		return isNew;
	}

	private final boolean rawAdd(T element) {
		final boolean isNew = (map.remove(element) == null);
		map.put(element, element);
		return isNew;
	}

	/**
	 * Adds all of the <code>elements</code>.
	 *
	 * @param elements the elements to add.
	 * @return <code>true</code> indicating the set has changed; adding items always
	 * re-inserts elements with the current timestamp.
	 */
	public boolean addAll(Collection<? extends T> elements) {
		synchronized(lock) {
			for (final T element : elements) {
				 rawAdd(element);
			}
		}
		return true;
	}

	/**
	 * Indicates whether the <code>element</code> is in the set.
	 *
	 * @param element the element to check for.
	 * @return <code>true</code> if the element is in the set; <code>false</code> otherwise.
	 */
	public boolean contains(T element) {
		synchronized(lock) {
			return map.containsKey(element);
		}
	}

	/**
	 * Removes and returns the last element, or <code>null</code> if the set is empty.
	 *
	 * @return the last element, or <code>null</code> if the set is empty.
	 */
	public T pollLast() {
		synchronized(lock) {
			return map.isEmpty() ?
				null :
				map.remove(map.lastKey());
		}
	}

	/**
	 * Removes the <code>element</code>.
	 *
	 * @param element the element to remove.
	 * @return <code>true</code> if set contained the element; <code>false</code> if not.
	 */
	public boolean remove(T element) {
		synchronized(lock) {
			return map.remove(element) != null;
		}
	}

	/**
	 * Returns the size of the set.
	 *
	 * @return the size of the set.
	 */
	public int size() {
		synchronized(lock) {
			return map.size();
		}
	}

	Observable<T> createObservable() {
		return Observable.create(new SyncOnSubscribe<Void,T>() {
			@Override
			protected Void generateState() { return null; }

			@Override
			protected Void next(Void state, Observer<? super T> observer) {
				T element;
				while ((element = pollLast()) == null) {
					try {
						synchronized (lock) {
							lock.wait();
						}
					}
					catch (InterruptedException exception) {}
				}
				observer.onNext(element);
				return null;
			}
		});
	}
}
