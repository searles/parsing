package at.searles.parsing.lexer

import at.searles.parsing.codepoint.FrameStream
import at.searles.parsing.codepoint.StringCodePointStream
import org.junit.Assert
import org.junit.Test

class FrameStreamTest {
    @Test
    fun testFrameIsEmpty() {
        val stream = FrameStream(StringCodePointStream("abcd"))
        Assert.assertEquals("", stream.frame.toString())
    }

    @Test
    fun testFrameToString() {
        val stream = FrameStream(StringCodePointStream("abcd"))

        Assert.assertEquals('a'.toInt(), stream.read())
        Assert.assertEquals('b'.toInt(), stream.read())

        stream.mark()

        Assert.assertEquals("ab", stream.frame.toString())
    }

    @Test
    fun testFrameEmptyAfterConsuming() {
        val stream = FrameStream(StringCodePointStream("abcd"))

        Assert.assertEquals('a'.toInt(), stream.read())
        Assert.assertEquals('b'.toInt(), stream.read())

        stream.mark()
        stream.next()

        Assert.assertEquals("", stream.frame.toString())
    }

    @Test
    fun testFrameStreamToString() {
        val stream = FrameStream(StringCodePointStream("abcd"))

        Assert.assertEquals('a'.toInt(), stream.read())
        Assert.assertEquals('b'.toInt(), stream.read())

        stream.mark()

        Assert.assertEquals('c'.toInt(), stream.read())

        Assert.assertEquals("ab", stream.frame.toString())

        stream.next()
        Assert.assertEquals('c'.toInt(), stream.read())
    }


    @Test
    fun testMultipleSetFrameEnd() {
        val stream = FrameStream(StringCodePointStream("abcd"))

        Assert.assertEquals('a'.toInt(), stream.read())
        stream.mark()
        Assert.assertEquals('b'.toInt(), stream.read())

        Assert.assertEquals("a", stream.frame.toString())
        stream.mark()
        Assert.assertEquals("ab", stream.frame.toString())
    }

    @Test
    fun testFrameReadAfterConsuming() {
        val stream = FrameStream(StringCodePointStream("abcd"))

        stream.read()
        stream.read()

        stream.mark()

        Assert.assertEquals('c'.toInt(), stream.read())

        stream.next()

        Assert.assertEquals('c'.toInt(), stream.read())
        stream.mark()

        Assert.assertEquals("c", stream.frame.toString())
    }
}