package at.searles.parsing.lexer.fsa

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
        val set = IntervalSet().apply { add((0 .. 0)) }
        Assert.assertFalse(set.contains(-1))
        Assert.assertTrue(set.contains(0))
        Assert.assertFalse(set.contains(1))
    }

    @Test
    fun testBoundsForOneItemInverted() {
        val set = IntervalSet().apply {
            add((0 .. 0))
        }.inverted(-10, 10)

        Assert.assertTrue(set.contains(-1))
        Assert.assertFalse(set.contains(0))
        Assert.assertTrue(set.contains(1))
    }

    @Test
    fun testMergeRight() {
        val set = IntervalSet().apply {
            add((0 .. 0))
            add((1 .. 1))
        }

        val iterator = set.iterator()

        val interval = iterator.next()

        Assert.assertEquals((0 until 2), interval)
        Assert.assertFalse(iterator.hasNext())
    }

    @Test
    fun testMergeLeft() {
        val set = IntervalSet().apply {
            add((1 .. 1))
            add((0 .. 0))
        }

        val iterator = set.iterator()

        val interval = iterator.next()

        Assert.assertEquals((0 until 2), interval)
        Assert.assertFalse(iterator.hasNext())
    }

    @Test
    fun testInsertInOrder() {
        val set = IntervalSet()

        set.add((1 until 2))
        set.add((3 until 4))
        set.add((5 until 6))

        val iterator = set.iterator()

        Assert.assertEquals((1 until 2), iterator.next())
        Assert.assertEquals((3 until 4), iterator.next())
        Assert.assertEquals((5 until 6), iterator.next())

        Assert.assertFalse(iterator.hasNext())
    }


    @Test
    fun testInsertInReverse() {
        val set = IntervalSet()

        set.add((5 until 6))
        set.add((3 until 4))
        set.add((1 until 2))

        val iterator = set.iterator()

        Assert.assertEquals((1 until 2), iterator.next())
        Assert.assertEquals((3 until 4), iterator.next())
        Assert.assertEquals((5 until 6), iterator.next())

        Assert.assertFalse(iterator.hasNext())
    }

    @Test
    fun testInsert231() {
        val set = IntervalSet()

        set.add((3 until 4))
        set.add((5 until 6))
        set.add((1 until 2))

        val iterator = set.iterator()

        Assert.assertEquals((1 until 2), iterator.next())
        Assert.assertEquals((3 until 4), iterator.next())
        Assert.assertEquals((5 until 6), iterator.next())

        Assert.assertFalse(iterator.hasNext())
    }

    @Test
    fun testInsert213() {
        val set = IntervalSet()

        set.add((3 until 4))
        set.add((1 until 2))
        set.add((5 until 6))

        val iterator = set.iterator()

        Assert.assertEquals((1 until 2), iterator.next())
        Assert.assertEquals((3 until 4), iterator.next())
        Assert.assertEquals((5 until 6), iterator.next())

        Assert.assertFalse(iterator.hasNext())
    }

    @Test
    fun testInsertBug() {
        val set = IntervalSet()

        set.add((5 until 6))
        set.add((7 until 8))
        set.add((1 until 2))
        set.add((3 until 4))

        val iterator = set.iterator()

        Assert.assertEquals((1 until 2), iterator.next())
        Assert.assertEquals((3 until 4), iterator.next())
        Assert.assertEquals((5 until 6), iterator.next())
        Assert.assertEquals((7 until 8), iterator.next())

        Assert.assertFalse(iterator.hasNext())
    }

    @Test
    fun testInsertLargeRange() {
        val set = IntervalSet()

        set.add((1 until 2))
        set.add((3 until 4))
        set.add((5 until 6))
        set.add((0 until 7))

        val iterator = set.iterator()

        Assert.assertEquals((0 until 7), iterator.next())

        Assert.assertFalse(iterator.hasNext())
    }

    @Test
    fun testInsertLargeRangeOuterOverlap() {
        val set = IntervalSet()

        set.add((1 until 2))
        set.add((3 until 4))
        set.add((5 until 6))
        set.add((2 until 5))

        val iterator = set.iterator()

        Assert.assertEquals((1 until 6), iterator.next())

        Assert.assertFalse(iterator.hasNext())
    }

    @Test
    fun testContains() {
        val set = IntervalSet()

        set.add((1 until 2))
        set.add((3 until 4))

        Assert.assertFalse(set.contains(0))
        Assert.assertTrue(set.contains(1))
        Assert.assertFalse(set.contains(2))
        Assert.assertTrue(set.contains(3))
        Assert.assertFalse(set.contains(4))
    }

    @Test
    fun testContainsAny() {
        val set = IntervalSet()

        set.add((1 until 2))
        set.add((3 until 4))

        val set2 = IntervalSet()
        set2.add((2 until 3))
        set2.add((4 until 5))

        Assert.assertFalse(set.containsAny(set2))
        Assert.assertFalse(set2.containsAny(set))

        set.add((2 until 3))

        Assert.assertTrue(set.containsAny(set2))
        Assert.assertTrue(set2.containsAny(set))
    }
}