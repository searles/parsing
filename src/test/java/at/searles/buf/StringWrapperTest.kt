package at.searles.buf

import org.junit.Assert
import org.junit.Test

class StringWrapperTest {
    private lateinit var str: StringWrapper

    @Test
    fun initialConditionTest() {
        withString("basic")
        Assert.assertEquals(0, str.position())
        Assert.assertEquals(0, str.frame.length.toLong())
    }

    @Test
    fun koalaTest() {
        withString("\uD83D\uDC28koala") // first char is the unicode koala
        Assert.assertEquals(0x1f428, str.next().toLong()) // UTF-16
        Assert.assertEquals('k'.toLong(), str.next().toLong())
        str.mark()
        Assert.assertEquals('o'.toLong(), str.next().toLong())
        str.advance()
        Assert.assertEquals('o'.toLong(), str.next().toLong())
    }

    @Test
    fun frameTest() {
        withString("abcdef") // first char is the unicode koala
        str.next()
        str.next()
        str.mark()
        str.advance()
        str.next()
        str.next()
        str.mark()
        str.next()
        Assert.assertEquals("cd", str.frame.toString())
    }

    @Test
    fun setPtrTest() {
        withString("abcdef") // first char is the unicode koala
        str.next()
        str.next()
        str.mark()
        str.advance()
        str.next()
        str.next()
        str.mark()
        str.next()
        str.setPositionTo(0)
        str.next()
        str.next()
        str.mark()
        str.next()
        Assert.assertEquals("ab", str.frame.toString())
    }

    private fun withString(str: String) {
        this.str = StringWrapper(str)
    }
}