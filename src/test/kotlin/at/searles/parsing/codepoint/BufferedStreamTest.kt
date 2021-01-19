package at.searles.parsing.codepoint

import org.junit.Assert
import org.junit.Test

class BufferedStreamTest {
    @Test
    fun testSetPosition() {
        val stream = BufferedStream.Impl(StringCodePointStream("abc"))

        Assert.assertEquals('a'.toInt(), stream.read())

        val index = stream.index

        Assert.assertEquals('b'.toInt(), stream.read())
        Assert.assertEquals('c'.toInt(), stream.read())
        Assert.assertEquals(-1, stream.read())

        stream.backtrackToIndex(index)

        Assert.assertEquals('b'.toInt(), stream.read())
        Assert.assertEquals('c'.toInt(), stream.read())
        Assert.assertEquals(-1, stream.read())
    }

    @Test
    fun testBufferTooSmall() {
        val stream = BufferedStream.Impl(StringCodePointStream("abcd"), 2)

        val startIndex = stream.index

        Assert.assertEquals('a'.toInt(), stream.read())
        Assert.assertEquals('b'.toInt(), stream.read())
        Assert.assertEquals('c'.toInt(), stream.read())

        try {
            stream.backtrackToIndex(startIndex)
            Assert.fail()
        } catch (e: BufferTooSmallException) {
            e.printStackTrace()
        }
    }

    @Test
    fun testBufferFullyUsed() {
        val stream = BufferedStream.Impl(StringCodePointStream("abcd"), 3)

        val startIndex = stream.index

        Assert.assertEquals('a'.toInt(), stream.read())
        Assert.assertEquals('b'.toInt(), stream.read())
        Assert.assertEquals('c'.toInt(), stream.read())

        stream.backtrackToIndex(startIndex)

        Assert.assertEquals('a'.toInt(), stream.read())
        Assert.assertEquals('b'.toInt(), stream.read())
        Assert.assertEquals('c'.toInt(), stream.read())
        Assert.assertEquals('d'.toInt(), stream.read())

        Assert.assertEquals(-1, stream.read())
    }

    @Test
    fun testSubstring() {
        val stream = BufferedStream.Impl(StringCodePointStream("abcd"), 3)

        Assert.assertEquals('a'.toInt(), stream.read())

        val startIndex = stream.index

        Assert.assertEquals('b'.toInt(), stream.read())
        Assert.assertEquals('c'.toInt(), stream.read())

        val length = (stream.index - startIndex).toInt()

        Assert.assertEquals("bc", stream.getString(startIndex, length))

        Assert.assertEquals('d'.toInt(), stream.read())

        Assert.assertEquals("bc", stream.getString(startIndex, length))
    }
}