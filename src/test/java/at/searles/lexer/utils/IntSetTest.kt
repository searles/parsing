package at.searles.lexer.utils

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
}