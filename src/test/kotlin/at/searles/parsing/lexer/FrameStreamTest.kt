package at.searles.parsing.lexer

import at.searles.parsing.codepoint.StringCodePointStream
import org.junit.Assert
import org.junit.Test

class FrameStreamTest {
    @Test
    fun testFrameIsEmpty() {
        val frameStream = FrameStream(StringCodePointStream("abcd"))
        Assert.assertEquals("", frameStream.getFrame())
    }

    @Test
    fun testFrameToString() {
        val frameStream = FrameStream(StringCodePointStream("abcd"))

        Assert.assertEquals('a'.toInt(), frameStream.read())
        Assert.assertEquals('b'.toInt(), frameStream.read())

        frameStream.setFrameEnd()

        Assert.assertEquals("ab", frameStream.getFrame())
    }

    @Test
    fun testFrameEmptyAfterConsuming() {
        val frameStream = FrameStream(StringCodePointStream("abcd"))

        Assert.assertEquals('a'.toInt(), frameStream.read())
        Assert.assertEquals('b'.toInt(), frameStream.read())

        frameStream.setFrameEnd()
        frameStream.consumeFrame()

        Assert.assertEquals("", frameStream.getFrame())
    }

    @Test
    fun testFrameStreamToString() {
        val frameStream = FrameStream(StringCodePointStream("abcd"))

        Assert.assertEquals('a'.toInt(), frameStream.read())
        Assert.assertEquals('b'.toInt(), frameStream.read())

        frameStream.setFrameEnd()

        frameStream.read()

        Assert.assertEquals("\"ab\": abc_d[0:2]", frameStream.toString())
    }


    @Test
    fun testMultipleSetFrameEnd() {
        val frameStream = FrameStream(StringCodePointStream("abcd"))

        Assert.assertEquals('a'.toInt(), frameStream.read())
        frameStream.setFrameEnd()
        Assert.assertEquals('b'.toInt(), frameStream.read())

        Assert.assertEquals("a", frameStream.getFrame())
        frameStream.setFrameEnd()
        Assert.assertEquals("ab", frameStream.getFrame())
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

        Assert.assertEquals("c", frameStream.getFrame())
    }
}