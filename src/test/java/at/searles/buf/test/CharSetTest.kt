package at.searles.buf.test

import at.searles.lexer.utils.Interval
import at.searles.regexp.CharSet
import org.junit.Assert
import org.junit.Test
import java.util.*

class CharSetTest {
    private var set: CharSet? = null

    @Test
    fun testCharSetInvert() {
        with(CharSet.chars('.'.toInt()))
        set = set!!.invert()
        val l: MutableList<Interval> = LinkedList()
        for (i in set!!) {
            l.add(i)
        }
        Assert.assertEquals(2, l.size.toLong())
        Assert.assertEquals('.'.toLong(), l[0].end.toLong())
        Assert.assertEquals('.'.toInt() + 1.toLong(), l[1].start.toLong())
    }

    private fun with(set: CharSet) {
        this.set = set
    }
}