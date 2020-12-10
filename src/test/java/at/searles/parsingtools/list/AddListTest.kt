package at.searles.parsingtools.list

import at.searles.parsing.ParserStream
import org.junit.Assert
import org.junit.Test

class AddListTest {
    @Test
    fun testAppendMayBeEmpty() {
        val cons = ListAppender<Int>(0)

        var l: List<Int>? = listOf(1, 2)

        Assert.assertEquals(2, cons.rightInverse(l!!))
        l = cons.leftInverse(l)
        Assert.assertEquals(1, cons.rightInverse(l!!))
        l = cons.leftInverse(l)

        Assert.assertNotNull(l)
        Assert.assertTrue(l!!.isEmpty())

        Assert.assertNull(cons.leftInverse(l))
        Assert.assertNull(cons.rightInverse(l))
    }

    @Test
    fun testAppendMayNotBeEmpty() {
        val cons = ListAppender<Int>(1)

        var l: List<Int>? = listOf(1, 2)

        Assert.assertEquals(2, cons.rightInverse(l!!))
        l = cons.leftInverse(l)

        Assert.assertEquals(1, l!!.size.toLong())

        Assert.assertNull(cons.leftInverse(l))
        Assert.assertNull(cons.rightInverse(l))
    }

    @Test
    fun testRleList() {
        val appenderRle = ListAppenderWithAmount<String>(2)

        val list = listOf("A", "B", "B")

        val pairB = Pair("B", 3)
        val list2 = appenderRle.apply(ParserStream.create(""), list, pairB)
        val pairC = Pair("C", 1)
        val list3 = appenderRle.apply(ParserStream.create(""), list2, pairC)
        val pairD = Pair("D", 4)
        val list4 = appenderRle.apply(ParserStream.create(""), list3, pairD)

        Assert.assertEquals(listOf("A", "B", "B", "B", "B", "B", "C", "D", "D", "D", "D"), list4)

        Assert.assertEquals(list3, appenderRle.leftInverse(list4))
        Assert.assertEquals(pairD, appenderRle.rightInverse(list4))

        Assert.assertEquals(list2, appenderRle.leftInverse(list3))
        Assert.assertEquals(pairC, appenderRle.rightInverse(list3))

        Assert.assertEquals(listOf("A", "B"), appenderRle.leftInverse(list2))
        Assert.assertEquals(Pair("B", 4), appenderRle.rightInverse(list2))

        Assert.assertNull(appenderRle.leftInverse(listOf("A", "B")))
        Assert.assertNull(appenderRle.rightInverse(listOf("A", "B")))
    }
}
