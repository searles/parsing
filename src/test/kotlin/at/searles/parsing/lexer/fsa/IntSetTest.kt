package at.searles.parsing.lexer.fsa

import org.junit.Assert
import org.junit.Test

class IntSetTest {
    @Test
    fun testAdd() {
        val set = IntSet()
        Assert.assertTrue(set.add(3))
        Assert.assertTrue(set.add(2))
        Assert.assertTrue(set.add(1))
        Assert.assertFalse(set.add(3))
    }

    @Test
    fun removeFromEmpty() {
        val set = IntSet()
        Assert.assertFalse(set.remove(1))
    }

    @Test
    fun removeEdgeCases() {
        val set = IntSet()
        set.add(1)
        set.add(2)
        set.add(3)
        Assert.assertTrue(set.remove(3))
        Assert.assertTrue(set.remove(1))
        Assert.assertFalse(set.remove(1))
        Assert.assertFalse(set.remove(3))
        Assert.assertTrue(set.remove(2))
        Assert.assertTrue(set.isEmpty)
    }

    @Test
    fun containsAnyTest() {
        val s0 = IntSet()
        val s1 = IntSet()
        s0.add(1)
        s0.add(2)
        s0.add(3)
        s1.add(4)
        s1.add(5)
        s1.add(6)
        Assert.assertFalse(s0.containsAny(s1))
        s1.add(3)
        Assert.assertTrue(s0.containsAny(s1))
    }

    @Test
    fun emptyAfterRetainAllTest() {
        val s0 = IntSet()
        val s1 = IntSet()
        s0.add(1)
        s0.add(2)
        s0.add(3)
        s1.add(4)
        s1.add(5)
        s1.add(6)
        s0.retainAll(s1)
        Assert.assertTrue(s0.isEmpty)
    }

    @Test
    fun retainAllTest() {
        val s0 = IntSet()
        val s1 = IntSet()
        s0.add(1)
        s0.add(2)
        s0.add(3)
        s1.add(3)
        s1.add(4)
        s1.add(5)
        s1.add(6)
        s0.retainAll(s1)
        Assert.assertEquals(3, s0[0])
        Assert.assertEquals(1, s0.size)
    }

    @Test
    fun testSizeAdjusted() {
        val s = IntSet(2)
        s.add(1)
        s.add(2)
        s.add(3)

        Assert.assertEquals(3, s.size)
        Assert.assertEquals(1, s.first)
        Assert.assertEquals(2, s[1])
        Assert.assertEquals(3, s.last)
    }

    @Test
    fun testFirstLast() {
        val s = IntSet(3)
        s.add(1)

        Assert.assertEquals(1, s.first)
        Assert.assertEquals(1, s.last)

        s.add(2)

        Assert.assertEquals(1, s.first)
        Assert.assertEquals(2, s.last)

        s.add(0)

        Assert.assertEquals(0, s.first)
        Assert.assertEquals(2, s.last)
    }

    @Test
    fun testCompareWithEmpty() {
        val s0 = IntSet(0)
        val s1 = IntSet(2)

        listOf(1, 2).forEach { s1.add(it) }

        Assert.assertTrue(s0 < s1)
        Assert.assertTrue(s1 > s0)
    }

    @Test
    fun testCompareWithPrefix() {
        val s0 = IntSet(1)
        val s1 = IntSet(2)

        listOf(1).forEach { s0.add(it) }
        listOf(1, 2).forEach { s1.add(it) }

        Assert.assertTrue(s0 < s1)
        Assert.assertTrue(s1 > s0)
    }

    @Test
    fun testCompare() {
        val s0 = IntSet(1)
        val s1 = IntSet(1)

        listOf(1).forEach { s0.add(it) }
        listOf(2).forEach { s1.add(it) }

        Assert.assertTrue(s0 < s1)
        Assert.assertTrue(s1 > s0)
    }

    @Test
    fun testEmptyIterator() {
        Assert.assertFalse(IntSet().iterator().hasNext())
    }

    @Test
    fun testIterator12() {
        val iterator = IntSet().run {
            add(2)
            add(1)
            iterator()
        }

        Assert.assertTrue(iterator.hasNext())
        Assert.assertEquals(1, iterator.next())
        Assert.assertTrue(iterator.hasNext())
        Assert.assertEquals(2, iterator.next())
        Assert.assertFalse(iterator.hasNext())
    }

    @Test
    fun testIndexOfFirstMatch_Distinct() {
        val s0 = IntSet().apply {
            add(2)
            add(1)
        }

        val s1 = IntSet().apply {
            add(3)
            add(4)
        }

        Assert.assertEquals(-1, s0.indexOfFirstMatch(s1))
    }

    @Test
    fun testIndexOfFirstMatch_Same() {
        val s0 = IntSet().apply {
            add(2)
            add(1)
        }

        val s1 = IntSet().apply {
            add(1)
            add(2)
        }

        Assert.assertEquals(0, s0.indexOfFirstMatch(s1))
    }


    @Test
    fun testIndexOfFirstMatch_Intersect() {
        val s0 = IntSet().apply {
            add(0)
            add(2)
            add(4)
        }

        val s1 = IntSet().apply {
            add(1)
            add(2)
            add(3)
        }

        Assert.assertEquals(1, s0.indexOfFirstMatch(s1))
    }
}