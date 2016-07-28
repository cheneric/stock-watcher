package cheneric.stockwatcher.util;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import rx.Observable;
import rx.Observer;
import rx.functions.Func1;
import rx.observables.SyncOnSubscribe;

public class OrderedConcurrentNavigableSet<T> {
	public static final int LIFO_MODE = 1;
	public static final int FIFO_MODE = 2;

	private final Func1<T,InsertionKeyOrder<T>> insertionKeyOrderFactory;
	private final ConcurrentNavigableMap<InsertionKeyOrder<T>,T> map = new ConcurrentSkipListMap<>();
	private final Object observableMonitor = new Object();

	@Getter
	private final Observable<T> observable = createObservable();

	/**
	 * Creates an instance in {@link #LIFO_MODE}.
	 */
	public OrderedConcurrentNavigableSet() { this(LIFO_MODE); }

	public OrderedConcurrentNavigableSet(int mode) {
		switch (mode) {
			case LIFO_MODE:
				insertionKeyOrderFactory = DescInsertionKeyOrder::new;
				break;
			case FIFO_MODE:
				insertionKeyOrderFactory = AscInsertionKeyOrder::new;
				break;
			default:
				throw new IllegalArgumentException("invalid mode: " + mode);
		}
	}

	/**
	 * Adds the element.
	 *
	 * @param element the element to add.
	 * @return <code>true</code> if the element was not already in the set; otherwise, returns
	 * <code>false</code>.
	 */
	public boolean add(T element) {
		final InsertionKeyOrder<T> key = wrapKey(element);
		// map does not support null values, so this checks if a previous value existed
		final boolean isNew = (map.remove(key) == null);
		map.put(key, element);
		synchronized (observableMonitor) {
			observableMonitor.notifyAll();
		}
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
		for (final T element : elements) {
			add(element);
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
		return map.containsKey(wrapKey(element));
	}

	/**
	 * Removes and returns the first element, or <code>null</code> if the set is empty.
	 *
	 * @return the first element, or <code>null</code> if the set is empty.
	 */
	public T pollFirst() {
		Map.Entry<InsertionKeyOrder<T>,T> firstEntry = map.pollFirstEntry();
		return firstEntry == null ? null : firstEntry.getValue();
	}

	/**
	 * Removes the <code>element</code>.
	 *
	 * @param element the element to remove.
	 * @return <code>true</code> if set contained the element; <code>false</code> if not.
	 */
	public boolean remove(T element) {
		// map does not support null values, so this checks if a previous value existed
		return map.remove(wrapKey(element)) != null;
	}

	/**
	 * Returns the size of the set.
	 *
	 * @return the size of the set.
	 */
	public int size() {
		return map.size();
	}

	protected InsertionKeyOrder wrapKey(T element) {
		return insertionKeyOrderFactory.call(element);
	}

	Observable<T> createObservable() {
		return Observable.create(new SyncOnSubscribe<Void,T>() {
			@Override
			protected Void generateState() { return null; }

			@Override
			protected Void next(Void state, Observer<? super T> observer) {
				T element;
				while ((element = pollFirst()) == null) {
					try {
						synchronized (observableMonitor) {
							observableMonitor.wait();
						}
					}
					catch (InterruptedException exception) {}
				}
				observer.onNext(element);
				return null;
			}
		});
	}

	protected static class AscInsertionKeyOrder<K> extends InsertionKeyOrder<K> {
		AscInsertionKeyOrder(K key) {
			super(key);
		}

		// for unit testing
		AscInsertionKeyOrder(K key, long timestamp) {
			super(key, timestamp);
		}

		@Override
		public int compareTo(InsertionKeyOrder<K> keyOrder) {
			return compareAsc(timestamp, keyOrder.timestamp);
		}
	}

	protected static class DescInsertionKeyOrder<K> extends InsertionKeyOrder<K> {
		DescInsertionKeyOrder(K key) {
			super(key);
		}

		// for unit testing
		DescInsertionKeyOrder(K key, long timestamp) {
			super(key, timestamp);
		}

		@Override
		public int compareTo(InsertionKeyOrder<K> keyOrder) {
			return -1 * compareAsc(timestamp, keyOrder.timestamp);
		}
	}

	/**
	 * <p>Note that unless overridden, subclasses of this class implement
	 * {@link #equals(Object)} and {@link #hashCode()} based only on the wrapped
	 * <code>key</code>, and {@link #compareTo(Object)} based only on the creation
	 * {@link #timestamp}.  Hence, subclasses will have a natural ordering that is
	 * inconsistent with equals.</p>
	 *
	 * @param <K> the key type.
	 */
	@RequiredArgsConstructor(access=AccessLevel.PROTECTED)  // for unit testing
	@EqualsAndHashCode(of = "key")
	@ToString
	protected static abstract class InsertionKeyOrder<K> implements Comparable<InsertionKeyOrder<K>> {
		protected final K key;
		protected final long timestamp;

		protected InsertionKeyOrder(K key) {
			this.key = key;
			this.timestamp = System.currentTimeMillis();
		}

		protected final int compareAsc(long long1, long long2) {
			return long1 < long2 ?
				-1 :
				long1 == long2 ? 0 : 1;
		}
	}
}
