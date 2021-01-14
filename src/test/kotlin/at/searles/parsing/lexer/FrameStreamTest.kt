package at.searles.parsing.lexer

import at.searles.parsing.codepoint.StringCodePointStream
import org.junit.Assert
import org.junit.Test

class FrameStreamTest {
    @Test
    fun testFrameIsEmpty() {
        val frameStream = FrameStream(StringCodePointStream("abcd"))
        Assert.assertEquals("", frameStream.frame.toString())
    }

    @Test
    fun testFrameToString() {
        val frameStream = FrameStream(StringCodePointStream("abcd"))

        Assert.assertEquals('a'.toInt(), frameStream.read())
        Assert.assertEquals('b'.toInt(), frameStream.read())

        frameStream.setFrameEnd()

        Assert.assertEquals("ab", frameStream.frame.toString())
    }

    @Test
    fun testFrameEmptyAfterConsuming() {
        val frameStream = FrameStream(StringCodePointStream("abcd"))

        Assert.assertEquals('a'.toInt(), frameStream.read())
        Assert.assertEquals('b'.toInt(), frameStream.read())

        frameStream.setFrameEnd()
        frameStream.consumeFrame()

        Assert.assertEquals("", frameStream.frame.toString())
    }

    @Test
    fun testMultipleSetFrameEnd() {
        val frameStream = FrameStream(StringCodePointStream("abcd"))

        Assert.assertEquals('a'.toInt(), frameStream.read())
        frameStream.setFrameEnd()
        Assert.assertEquals('b'.toInt(), frameStream.read())

        Assert.assertEquals("a", frameStream.frame.toString())
        frameStream.setFrameEnd()
        Assert.assertEquals("ab", frameStream.frame.toString())
    }

    @Test
    fun testFrameReadAfterConsuming() {
        val frameStream = FrameStream(StringCodePointStream("abcd"))

        frameStream.read()
        frameStream.read()

        frameStream.setFrameEnd()

        Assert.assertEquals('c'.toInt(), frameStream.read())

        frameStream.consumeFrame()

        Assert.assertEquals('c'.toInt(), frameStream.read())
        frameStream.setFrameEnd()

        Assert.assertEquals("c", frameStream.frame.toString())
    }
}