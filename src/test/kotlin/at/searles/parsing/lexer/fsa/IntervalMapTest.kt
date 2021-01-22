package at.searles.parsing.lexer.fsa

import org.junit.Assert
import org.junit.Test

class IntervalMapTest {
    @Test
    fun testGet() {
        val map = IntervalMap<String>()

        map.add((0 until 2), "a")

        Assert.assertNull(map[-1])
        Assert.assertEquals("a", map[0])
        Assert.assertEquals("a", map[1])
        Assert.assertNull(map[2])
    }

    @Test
    fun testAddNoIntersect() {
        val map = IntervalMap<String>()

        map.add((0 until 2), "a")
        map.add((4 until 6), "b")

        Assert.assertNull(map[-1])
        Assert.assertEquals("a", map[0])
        Assert.assertEquals("a", map[1])
        Assert.assertNull(map[2])
        Assert.assertNull(map[3])
        Assert.assertEquals("b", map[4])
        Assert.assertEquals("b", map[5])
        Assert.assertNull(map[6])
    }

    @Test
    fun testAddNoIntersectButTouch() {
        val map = IntervalMap<String>()

        map.add((0 until 2), "a")
        map.add((2 until 4), "b")

        Assert.assertNull(map[-1])
        Assert.assertEquals("a", map[0])
        Assert.assertEquals("a", map[1])
        Assert.assertEquals("b", map[2])
        Assert.assertEquals("b", map[3])
        Assert.assertNull(map[4])
    }

    @Test
    fun testIntersect() {
        val map = IntervalMap<String>()

        map.add((0 until 2), "a")
        map.add((1 until 4), "b") { s1, s2 -> s1 + s2 }
        map.add((3 until 5), "c") { s1, s2 -> s1 + s2 }

        Assert.assertNull(map[-1])
        Assert.assertEquals("a", map[0])
        Assert.assertEquals("ab", map[1])
        Assert.assertEquals("b", map[2])
        Assert.assertEquals("bc", map[3])
        Assert.assertEquals("c", map[4])
        Assert.assertNull(map[5])
    }


    @Test
    fun testInsertLargeRange() {
        val map = IntervalMap<String>()

        map.add((1 until 2), "a")
        map.add((3 until 4), "b")
        map.add((5 until 6), "c")
        map.add((0 until 7), "d") { s1, s2 -> s1 + s2}

        Assert.assertNull(map[-1])
        Assert.assertEquals("d", map[0])
        Assert.assertEquals("ad", map[1])
        Assert.assertEquals("d", map[2])
        Assert.assertEquals("bd", map[3])
        Assert.assertEquals("d", map[4])
        Assert.assertEquals("cd", map[5])
        Assert.assertEquals("d", map[6])
        Assert.assertNull(map[7])
    }

    @Test
    fun testInsertLargeRangeOuterRange() {
        val map = IntervalMap<String>()

        map.add((1 until 2), "a")
        map.add((3 until 4), "b")
        map.add((5 until 6), "c")
        map.add((2 until 6), "d") { s1, s2 -> s1 + s2}

        Assert.assertNull(map[0])
        Assert.assertEquals("a", map[1])
        Assert.assertEquals("d", map[2])
        Assert.assertEquals("bd", map[3])
        Assert.assertEquals("d", map[4])
        Assert.assertEquals("cd", map[5])
        Assert.assertNull(map[6])
    }

}