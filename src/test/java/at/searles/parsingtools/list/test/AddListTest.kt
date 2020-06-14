package at.searles.parsingtools.list.test

import at.searles.parsingtools.list.ListAppender
import org.junit.Assert
import org.junit.Test

import java.util.Arrays

class AddListTest {
    @Test
    fun testAppendMayBeEmpty() {
        val cons = ListAppender<Int>(0)

        var l: List<Int>? = Arrays.asList(1, 2)

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

        var l: List<Int>? = Arrays.asList(1, 2)

        Assert.assertEquals(2, cons.rightInverse(l!!))
        l = cons.leftInverse(l)

        Assert.assertEquals(1, l!!.size.toLong())

        Assert.assertNull(cons.leftInverse(l))
        Assert.assertNull(cons.rightInverse(l))
    }
}
