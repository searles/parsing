package at.searles.parsing.parser.tools

import at.searles.parsing.parser.tools.ReducerBuilders.newInstance
import org.junit.Assert
import org.junit.Test

class NewInstanceTest {
    @Test
    fun testCreateDataClassInstanceFromPair() {
        data class A(val s: String, val i: Int)

        val creator = newInstance<A>().from<Pair<String, Int>>()

        val result = creator.convert(Pair("Hello", 123))
        Assert.assertEquals(A("Hello", 123), result)
    }

    @Test
    fun testCreatePairFromDataClassInstance() {
        data class A(val s: String, val i: Int)

        val creator = newInstance<A>().from<Pair<String, Int>>()

        val result = creator.invert(A("Hello", 123))
        Assert.assertEquals(Pair("Hello", 123), result.value)
    }

    @Test
    fun testCreateDataClassInstanceFromTwoPair() {
        data class A(val s: String, val i: Int, val t: List<Int>)
        val a = A("Hello", 123, listOf(1, 2, 3))
        val pairs = Pair(Pair("Hello", 123), listOf(1, 2, 3))

        val creator = newInstance<A>().from<Pair<Pair<String, Int>, List<Int>>>()

        val result = creator.convert(pairs)
        Assert.assertEquals(a, result)
    }

    @Test
    fun testCreateTwoPairFromDataClassInstance() {
        data class A(val s: String, val i: Int, val t: List<Int>)
        val a = A("Hello", 123, listOf(1, 2, 3))
        val pairs = Pair(Pair("Hello", 123), listOf(1, 2, 3))

        val creator = newInstance<A>().from<Pair<Pair<String, Int>, List<Int>>>()

        val result = creator.invert(a)
        Assert.assertEquals(pairs, result.value)
    }
}