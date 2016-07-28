package cheneric.stockwatcher.util;

import cheneric.stockwatcher.util.OrderedConcurrentNavigableSet.AscInsertionKeyOrder;

public class AscInsertionKeyOrderTest extends BaseInsertionKeyOrderTest {
	@Override
	protected <K> AscInsertionKeyOrder<K> createInsertionKeyOrder(K key, long timestamp) {
		return new AscInsertionKeyOrder(key, timestamp);
	}

	@Override
	protected int getCompareToMultiplier() { return 1; }
}
