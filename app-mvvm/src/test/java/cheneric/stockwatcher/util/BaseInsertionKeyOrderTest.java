package cheneric.stockwatcher.util;

import static org.junit.Assert.*;

import org.junit.Test;

import cheneric.stockwatcher.util.OrderedConcurrentNavigableSet.InsertionKeyOrder;

abstract class BaseInsertionKeyOrderTest {
	@Test
	public void testCompareAscending() {
		final InsertionKeyOrder<String> keyOrder = createInsertionKeyOrder("testCompareAsc", 0);
		assertTrue(keyOrder.compareAsc(Integer.MIN_VALUE, Integer.MAX_VALUE) < 0);
		assertTrue(keyOrder.compareAsc(0, Long.MAX_VALUE) < 0);
		assertTrue(keyOrder.compareAsc(Integer.MAX_VALUE, Long.MAX_VALUE) < 0);
		assertTrue(keyOrder.compareAsc(Long.MIN_VALUE, -149) < 0);
		assertTrue(keyOrder.compareAsc(Long.MIN_VALUE, 18049) < 0);
		assertTrue(keyOrder.compareAsc(Long.MAX_VALUE, -14) > 0);
		assertTrue(keyOrder.compareAsc(Long.MAX_VALUE, 9) > 0);
		assertTrue(keyOrder.compareAsc(Long.MIN_VALUE, Long.MIN_VALUE) == 0);
		assertTrue(keyOrder.compareAsc(-1111, -1111) == 0);
		assertTrue(keyOrder.compareAsc(0, 0) == 0);
		assertTrue(keyOrder.compareAsc(12, 12) == 0);
		assertTrue(keyOrder.compareAsc(Long.MAX_VALUE, Long.MAX_VALUE) == 0);
	}

	/**
	 * Tests that {@link InsertionKeyOrder#compareTo(Object)} only
	 * takes into account the {@link InsertionKeyOrder#timestamp}.
	 */
	@Test
	public void testCompareTo() {
		final int resultMultiplier = getCompareToMultiplier();
		final long NOW = System.currentTimeMillis();
		final InsertionKeyOrder<String> nowKeyOrder1 = createInsertionKeyOrder("nowKeyOrder1", NOW);
		final InsertionKeyOrder<String> nowKeyOrder2 = createInsertionKeyOrder("nowKeyOrder2", NOW);
		final InsertionKeyOrder<String> laterKeyOrder = createInsertionKeyOrder("laterKeyOrder", NOW + 5000);
		assertTrue(resultMultiplier * nowKeyOrder1.compareTo(laterKeyOrder) < 0);
		assertTrue(resultMultiplier * nowKeyOrder1.compareTo(nowKeyOrder2) == 0);
		assertTrue(resultMultiplier * nowKeyOrder2.compareTo(nowKeyOrder1) == 0);
		assertTrue(resultMultiplier * laterKeyOrder.compareTo(laterKeyOrder) == 0);
		assertTrue(resultMultiplier * laterKeyOrder.compareTo(nowKeyOrder1) > 0);
	}

	/**
	 * Tests that {@link InsertionKeyOrder#equals(Object)} only
	 * takes into account the {@link InsertionKeyOrder#key}.
	 */
	@Test
	public void testEquals() {
		final String KEY_1 = "key 1";
		final String KEY_2 = "key 2";
		final long TIMESTAMP_1 = System.currentTimeMillis();
		final long TIMESTAMP_2 = TIMESTAMP_1 / 2;
		assertTrue(createInsertionKeyOrder(KEY_1, TIMESTAMP_1).equals(createInsertionKeyOrder(KEY_1, TIMESTAMP_1)));
		assertTrue(createInsertionKeyOrder(KEY_1, TIMESTAMP_1).equals(createInsertionKeyOrder(KEY_1, TIMESTAMP_2)));
		assertFalse(createInsertionKeyOrder(KEY_1, TIMESTAMP_1).equals(createInsertionKeyOrder(KEY_2, TIMESTAMP_1)));
	}

	/**
	 * Tests that {@link InsertionKeyOrder#hashCode()} only
	 * takes into account the {@link InsertionKeyOrder#key}.
	 */
	@Test
	public void testHashCode() {
		final String KEY_1 = "key 1";
		final String KEY_2 = "key 2";
		final long TIMESTAMP_1 = System.currentTimeMillis();
		final long TIMESTAMP_2 = TIMESTAMP_1 / 2;
		assertEquals(createInsertionKeyOrder(KEY_1, TIMESTAMP_1).hashCode(), createInsertionKeyOrder(KEY_1, TIMESTAMP_1).hashCode());
		assertEquals(createInsertionKeyOrder(KEY_1, TIMESTAMP_1).hashCode(), createInsertionKeyOrder(KEY_1, TIMESTAMP_2).hashCode());
		assertNotEquals(createInsertionKeyOrder(KEY_1, TIMESTAMP_1).hashCode(), createInsertionKeyOrder(KEY_2, TIMESTAMP_1).hashCode());
	}

	protected abstract <K> InsertionKeyOrder<K> createInsertionKeyOrder(K key, long timestamp);

	/**
	 * <p>Returns the factor to multiply the result of
	 * {@link InsertionKeyOrder#compareTo(Object)} by before checking the expected results:</p>
	 *
	 * <ul>
	 *   <li><code>1</code> for {@link OrderedConcurrentNavigableSet.AscInsertionKeyOrder}</li>
	 *   <li><code>-1</code></li> for {@link OrderedConcurrentNavigableSet.DescInsertionKeyOrder}
	 * </ul>
	 *
	 * @return the factor to multiply the result of <code>compareTo()</code> by.
	 */
	protected abstract int getCompareToMultiplier();
}
