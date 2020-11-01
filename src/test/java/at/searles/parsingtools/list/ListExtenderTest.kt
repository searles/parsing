package at.searles.parsingtools.list

import at.searles.parsing.Mapping.Companion.cast
import at.searles.parsing.ParserStream
import org.junit.Assert
import org.junit.Test

class ListExtenderTest {
    interface C
    data class A(val n: Int): C
    data class B(val n: String): C

    @Test
    fun simpleTest() {
        val aList = listOf(A(1), A(2))
        val bList = listOf(B("a"), B("b"), B("c"))
        val aList2 = listOf(A(3))

        val aExtender = ListExtender<A, C>(cast())
        val bExtender = ListExtender<B, C>(cast())

        val emptyStream = ParserStream.create("")

        val cList1 = aExtender.apply(emptyStream, emptyList(), aList)
        val cList2 = bExtender.apply(emptyStream, cList1, bList)
        val cList3 = aExtender.apply(emptyStream, cList2, aList2)

        Assert.assertNull(bExtender.leftInverse(cList3))
        Assert.assertNull(bExtender.rightInverse(cList3))

        Assert.assertEquals(cList2, aExtender.leftInverse(cList3))
        Assert.assertEquals(aList2, aExtender.rightInverse(cList3))

        Assert.assertEquals(cList1, bExtender.leftInverse(cList2))
        Assert.assertEquals(bList, bExtender.rightInverse(cList2))

        Assert.assertTrue(aExtender.leftInverse(cList1)!!.isEmpty())
        Assert.assertEquals(aList, aExtender.rightInverse(cList1))
    }
}