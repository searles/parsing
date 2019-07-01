package at.searles.buf.test;

import at.searles.lexer.utils.IntervalSet;
import org.junit.Assert;
import org.junit.Test;


public class IntervalSetTest {

	private IntervalSet<Integer> set;
	private IntervalSet<Integer> set2;
	
	private void withSet(IntervalSet<Integer> set) {
		this.set = set;
	}

	@Test
	public void testCopy() {
		withSet(new IntervalSet<Integer>()
				.add(0, 3, 100, null)
				.add(6, 9, 200, null)
		);

		// act
		set2 = set.copy(a -> 1 + a);

		// assert
		Assert.assertEquals(new IntervalSet<Integer>()
				.add(0, 3, 101, null)
				.add(6, 9, 201, null), set2);
	}

	@Test
	public void testCopyMapToConstant() {
		withSet(new IntervalSet<Integer>()
				.add(0, 3, 100, null)
				.add(3, 6, 200, null)
		);

		// act
		set2 = set.copy(a -> 300);

		// assert
		Assert.assertEquals(new IntervalSet<Integer>()
				.add(0, 6, 300, null), set2);
	}

	@Test
	public void testIterSetValue() {
		withSet(new IntervalSet<Integer>()
				.add(0, 3, 100, null)
				.add(3, 6, 200, null)
				.add(6, 9, 100, null)
		);
		
		// act
		IntervalSet.Iter<Integer> it = set.iterator();
		
		it.next();
		it.next();
		it.setValue(100);
		
		// assert
		Assert.assertEquals(new IntervalSet<Integer>()
				.add(0, 9, 100, null), set);
	}

	@Test
	public void testIsEmpty() {
		withSet(new IntervalSet<Integer>());

		Assert.assertTrue(set.isEmpty());

		set.add(1,  2, 100, null);

		Assert.assertFalse(set.isEmpty());
	}

	@Test
	public void testFindInEmpty() {
		withSet(new IntervalSet<Integer>());

		Assert.assertFalse(set.contains(1));
		Assert.assertNull(set.find(1));
	}

	@Test
	public void testAddIntervalSetToEmptySet() {
		withSet(new IntervalSet<>());

		set2 = new IntervalSet<Integer>().add(0, 1, 100, null);

		set.add(set2, null);

		Assert.assertTrue(set.contains(0));
		Assert.assertFalse(set.contains(1));
		Assert.assertFalse(set.contains(-1));
	}

	@Test
	public void testContains() {
		withSet(new IntervalSet<Integer>()
				.add(0, 3, 100, null)
				.add(3, 6, 200, null)
				.add(7, 9, 100, null)
		);
		
		Assert.assertFalse(set.contains(-1));
		Assert.assertTrue(set.find(0).equals(100));
		Assert.assertTrue(set.find(2).equals(100));
		Assert.assertTrue(set.find(3).equals(200));
		Assert.assertFalse(set.contains(6));
		Assert.assertTrue(set.find(7).equals(100));
		Assert.assertTrue(set.find(8).equals(100));
		Assert.assertFalse(set.contains(9));
	}

	@Test
	public void testSimpleOverlap() {
		IntervalSet<Integer> set = new IntervalSet<>();

		set.add(1, 6, 111, (a, b) -> a + b);
		set.add(4, 9, 222, (a, b) -> a + b);

		Assert.assertEquals(new IntervalSet<Integer>()
				.add(1, 4, 111, null)
				.add(4, 6, 333, null)
				.add(6, 9, 222, null), set);
	}

	@Test
	public void testComplexOverlap() {
		IntervalSet<Integer> set = new IntervalSet<>();

		set.add(1, 4, 111, (a, b) -> a + b);
		set.add(7, 10, 444, (a, b) -> a + b);
		set.add(4, 7, 222, (a, b) -> a + b);

		set.add(1, 10, 1, (a, b) -> a + b);

		Assert.assertEquals(new IntervalSet<Integer>()
				.add(1, 4, 112, null)
				.add(4, 7, 223, null)
				.add(7, 10, 445, null), set);
	}

	@Test
	public void testSameOverlap() {
		IntervalSet<Integer> set = new IntervalSet<>();

		set.add(1, 4, 111, (a, b) -> a + b);
		set.add(1, 4, 222, (a, b) -> a + b);

		Assert.assertEquals(new IntervalSet<Integer>()
				.add(1, 4, 333, null), set);
	}

	@Test
	public void testSameBack() {
		IntervalSet<Integer> set = new IntervalSet<>();

		set.add(1, 7, 111, (a, b) -> a + b);
		set.add(4, 7, 222, (a, b) -> a + b);

		Assert.assertEquals(new IntervalSet<Integer>()
				.add(1, 4, 111, null)
				.add(4, 7, 333, null), set);
	}

	@Test
	public void testSameFront() {
		IntervalSet<Integer> set = new IntervalSet<>();

		set.add(1, 7, 111, (a, b) -> a + b);
		set.add(1, 4, 222, (a, b) -> a + b);

		Assert.assertEquals(new IntervalSet<Integer>()
				.add(1, 4, 333, null)
				.add(4, 7, 111, null), set);
	}

	@Test
	public void testFusion() {
		IntervalSet<Integer> set = new IntervalSet<>();

		set.add(1, 4, 111, null);
		set.add(7, 10, 111, null);
		set.add(4, 7, 111, null);

		Assert.assertEquals(new IntervalSet<Integer>()
				.add(1, 10, 111, null), set);
	}

	public void testFindMultipleIntervals() {
		IntervalSet<Integer> set = new IntervalSet<>();

		set.add(1, 4, 111, (a, b) -> a * b);
		set.add(7, 10, 111, (a, b) -> a * b);

		Assert.assertFalse(set.contains(0));
		Assert.assertEquals((Integer) 111, set.find(1));
		Assert.assertEquals((Integer) 111, set.find(3));
		Assert.assertFalse(set.contains(4));
		Assert.assertFalse(set.contains(6));
		Assert.assertEquals((Integer) 111, set.find(7));
		Assert.assertEquals((Integer) 111, set.find(9));
		Assert.assertFalse(set.contains(10));
	}

}
