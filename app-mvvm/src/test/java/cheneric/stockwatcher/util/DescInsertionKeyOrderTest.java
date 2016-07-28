package cheneric.stockwatcher.util;

import cheneric.stockwatcher.util.OrderedConcurrentNavigableSet.DescInsertionKeyOrder;

public class DescInsertionKeyOrderTest extends BaseInsertionKeyOrderTest {
	@Override
	protected <K> DescInsertionKeyOrder<K> createInsertionKeyOrder(K key, long timestamp) {
		return new DescInsertionKeyOrder(key, timestamp);
	}

	@Override
	protected int getCompareToMultiplier() { return -1; }
}
