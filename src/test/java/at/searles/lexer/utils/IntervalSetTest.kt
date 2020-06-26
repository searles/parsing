package at.searles.lexer.utils

import org.junit.Assert
import org.junit.Test

class IntervalSetTest {
    @Test
    fun testEmptyIntervalSet() {
        val set = IntervalSet()
        Assert.assertFalse(set.contains(0))
    }

    @Test
    fun testBoundsForOneItem() {
        val set = IntervalSet().apply { add(Interval(0)) }
        Assert.assertFalse(set.contains(-1))
        Assert.assertTrue(set.contains(0))
        Assert.assertFalse(set.contains(1))
    }

    @Test
    fun testBoundsForOneItemInverted() {
        val set = IntervalSet().apply {
            add(Interval(0))
        }.inverted(-10, 10)

        Assert.assertTrue(set.contains(-1))
        Assert.assertFalse(set.contains(0))
        Assert.assertTrue(set.contains(1))
    }

    @Test
    fun testMergeRight() {
        val set = IntervalSet().apply {
            add(Interval(0))
            add(Interval(1))
        }

        val iterator = set.iterator()

        val interval = iterator.next()

        Assert.assertEquals(Interval(0, 2), interval)
        Assert.assertFalse(iterator.hasNext())
    }

    @Test
    fun testMergeLeft() {
        val set = IntervalSet().apply {
            add(Interval(1))
            add(Interval(0))
        }

        val iterator = set.iterator()

        val interval = iterator.next()

        Assert.assertEquals(Interval(0, 2), interval)
        Assert.assertFalse(iterator.hasNext())
    }

    @Test
    fun testInsertInOrder() {
        val set = IntervalSet()

        set.add(Interval(1, 2))
        set.add(Interval(3, 4))
        set.add(Interval(5, 6))

        val iterator = set.iterator()

        Assert.assertEquals(Interval(1, 2), iterator.next())
        Assert.assertEquals(Interval(3, 4), iterator.next())
        Assert.assertEquals(Interval(5, 6), iterator.next())

        Assert.assertFalse(iterator.hasNext())
    }


    @Test
    fun testInsertInReverse() {
        val set = IntervalSet()

        set.add(Interval(5, 6))
        set.add(Interval(3, 4))
        set.add(Interval(1, 2))

        val iterator = set.iterator()

        Assert.assertEquals(Interval(1, 2), iterator.next())
        Assert.assertEquals(Interval(3, 4), iterator.next())
        Assert.assertEquals(Interval(5, 6), iterator.next())

        Assert.assertFalse(iterator.hasNext())
    }

    @Test
    fun testInsert231() {
        val set = IntervalSet()

        set.add(Interval(3, 4))
        set.add(Interval(5, 6))
        set.add(Interval(1, 2))

        val iterator = set.iterator()

        Assert.assertEquals(Interval(1, 2), iterator.next())
        Assert.assertEquals(Interval(3, 4), iterator.next())
        Assert.assertEquals(Interval(5, 6), iterator.next())

        Assert.assertFalse(iterator.hasNext())
    }

    @Test
    fun testInsert213() {
        val set = IntervalSet()

        set.add(Interval(3, 4))
        set.add(Interval(1, 2))
        set.add(Interval(5, 6))

        val iterator = set.iterator()

        Assert.assertEquals(Interval(1, 2), iterator.next())
        Assert.assertEquals(Interval(3, 4), iterator.next())
        Assert.assertEquals(Interval(5, 6), iterator.next())

        Assert.assertFalse(iterator.hasNext())
    }

    @Test
    fun testInsertBug() {
        val set = IntervalSet()

        set.add(Interval(5, 6))
        set.add(Interval(7, 8))
        set.add(Interval(1, 2))
        set.add(Interval(3, 4))

        val iterator = set.iterator()

        Assert.assertEquals(Interval(1, 2), iterator.next())
        Assert.assertEquals(Interval(3, 4), iterator.next())
        Assert.assertEquals(Interval(5, 6), iterator.next())
        Assert.assertEquals(Interval(7, 8), iterator.next())

        Assert.assertFalse(iterator.hasNext())
    }

    @Test
    fun testContains() {
        val set = IntervalSet()

        set.add(Interval(1, 2))
        set.add(Interval(3, 4))

        Assert.assertFalse(set.contains(0))
        Assert.assertTrue(set.contains(1))
        Assert.assertFalse(set.contains(2))
        Assert.assertTrue(set.contains(3))
        Assert.assertFalse(set.contains(4))
    }

    @Test
    fun testContainsAny() {
        val set = IntervalSet()

        set.add(Interval(1, 2))
        set.add(Interval(3, 4))

        val set2 = IntervalSet()
        set2.add(Interval(2, 3))
        set2.add(Interval(4, 5))

        Assert.assertFalse(set.containsAny(set2))
        Assert.assertFalse(set2.containsAny(set))

        set.add(Interval(2, 3))

        Assert.assertTrue(set.containsAny(set2))
        Assert.assertTrue(set2.containsAny(set))
    }
}