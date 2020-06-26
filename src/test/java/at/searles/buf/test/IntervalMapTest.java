package at.searles.buf.test;

import at.searles.lexer.utils.IntervalMap;
import org.junit.Assert;
import org.junit.Test;


public class IntervalMapTest {

    private IntervalMap<Integer> set;
    private IntervalMap<Integer> set2;

    private void withSet(IntervalMap<Integer> set) {
        this.set = set;
    }

    @Test
    public void testCopy() {
        withSet(new IntervalMap<Integer>()
                .add(0, 3, 100, null)
                .add(6, 9, 200, null)
        );

        // act
        set2 = set.copy(a -> 1 + a);

        // assert
        Assert.assertEquals(new IntervalMap<Integer>()
                .add(0, 3, 101, null)
                .add(6, 9, 201, null), set2);
    }

    @Test
    public void testCopyMapToConstant() {
        withSet(new IntervalMap<Integer>()
                .add(0, 3, 100, null)
                .add(3, 6, 200, null)
        );

        // act
        set2 = set.copy(a -> 300);

        // assert
        Assert.assertEquals(new IntervalMap<Integer>()
                .add(0, 6, 300, null), set2);
    }

    @Test
    public void testIterSetValue() {
        withSet(new IntervalMap<Integer>()
                .add(0, 3, 100, null)
                .add(3, 6, 200, null)
                .add(6, 9, 100, null)
        );

        // act
        IntervalMap.Iter<Integer> it = set.iterator();

        it.next();
        it.next();
        it.setValue(100);

        // assert
        Assert.assertEquals(new IntervalMap<Integer>()
                .add(0, 9, 100, null), set);
    }

    @Test
    public void testIsEmpty() {
        withSet(new IntervalMap<>());

        Assert.assertTrue(set.isEmpty());

        set.add(1, 2, 100, null);

        Assert.assertFalse(set.isEmpty());
    }

    @Test
    public void testFindInEmpty() {
        withSet(new IntervalMap<>());

        Assert.assertFalse(set.contains(1));
        Assert.assertNull(set.find(1));
    }

    @Test
    public void testAddIntervalSetToEmptySet() {
        withSet(new IntervalMap<>());

        set2 = new IntervalMap<Integer>().add(0, 1, 100, null);

        set.add(set2, null);

        Assert.assertTrue(set.contains(0));
        Assert.assertFalse(set.contains(1));
        Assert.assertFalse(set.contains(-1));
    }

    @Test
    public void testContains() {
        withSet(new IntervalMap<Integer>()
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
        IntervalMap<Integer> set = new IntervalMap<>();

        set.add(1, 6, 111, Integer::sum);
        set.add(4, 9, 222, Integer::sum);

        Assert.assertEquals(new IntervalMap<Integer>()
                .add(1, 4, 111, null)
                .add(4, 6, 333, null)
                .add(6, 9, 222, null), set);
    }

    @Test
    public void testComplexOverlap() {
        IntervalMap<Integer> set = new IntervalMap<>();

        set.add(1, 4, 111, Integer::sum);
        set.add(7, 10, 444, Integer::sum);
        set.add(4, 7, 222, Integer::sum);

        set.add(1, 10, 1, Integer::sum);

        Assert.assertEquals(new IntervalMap<Integer>()
                .add(1, 4, 112, null)
                .add(4, 7, 223, null)
                .add(7, 10, 445, null), set);
    }

    @Test
    public void testSameOverlap() {
        IntervalMap<Integer> set = new IntervalMap<>();

        set.add(1, 4, 111, Integer::sum);
        set.add(1, 4, 222, Integer::sum);

        Assert.assertEquals(new IntervalMap<Integer>()
                .add(1, 4, 333, null), set);
    }

    @Test
    public void testSameBack() {
        IntervalMap<Integer> set = new IntervalMap<>();

        set.add(1, 7, 111, Integer::sum);
        set.add(4, 7, 222, Integer::sum);

        Assert.assertEquals(new IntervalMap<Integer>()
                .add(1, 4, 111, null)
                .add(4, 7, 333, null), set);
    }

    @Test
    public void testSameFront() {
        IntervalMap<Integer> set = new IntervalMap<>();

        set.add(1, 7, 111, Integer::sum);
        set.add(1, 4, 222, Integer::sum);

        Assert.assertEquals(new IntervalMap<Integer>()
                .add(1, 4, 333, null)
                .add(4, 7, 111, null), set);
    }

    @Test
    public void testFusion() {
        IntervalMap<Integer> set = new IntervalMap<>();

        set.add(1, 4, 111, null);
        set.add(7, 10, 111, null);
        set.add(4, 7, 111, null);

        Assert.assertEquals(new IntervalMap<Integer>()
                .add(1, 10, 111, null), set);
    }

    public void testFindMultipleIntervals() {
        IntervalMap<Integer> set = new IntervalMap<>();

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
