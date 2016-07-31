package cheneric.stockwatcher.util;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class LinkedSetTest {
	private LinkedSet<String> linkedSet;

	@Before
	public void setUp() {
		linkedSet = new LinkedSet<String>();
	}

	@Test
	public void testAdd() {
		final String STRING1 = "string1";
		final String STRING2 = "string2";
		linkedSet.add(STRING1);
		assertEquals(1, linkedSet.size());
		linkedSet.add(STRING1);
		assertEquals(1, linkedSet.size());
		linkedSet.add(STRING2);
		assertEquals(2, linkedSet.size());
		linkedSet.add(STRING2);
		assertEquals(2, linkedSet.size());
		linkedSet.add(STRING1);
		assertEquals(2, linkedSet.size());
	}

	@Test
	public void testPollLast() {
		final String STRING1 = "string1";
		final String STRING2 = "string2";

		assertNull(linkedSet.pollLast());
		linkedSet.add(STRING1);
		assertEquals(STRING1, linkedSet.pollLast());
		assertNull(linkedSet.pollLast());
		// idempotent
		assertNull(linkedSet.pollLast());

		linkedSet.add(STRING1);
		linkedSet.add(STRING2);
		assertEquals(STRING2, linkedSet.pollLast());
		assertEquals(STRING1, linkedSet.pollLast());
		assertNull(linkedSet.pollLast());

		linkedSet.add(STRING1);
		linkedSet.add(STRING2);
		linkedSet.add(STRING2);
		linkedSet.add(STRING1);
		assertEquals(STRING1, linkedSet.pollLast());
		assertEquals(STRING2, linkedSet.pollLast());
		assertNull(linkedSet.pollLast());
	}
}
