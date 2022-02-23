package at.searles.parsing.codepoint

import org.junit.Assert
import org.junit.Test

class BufferedStreamTest {
    @Test
    fun testSetPosition() {
        val stream = IndexedStream.of(StringCodePointStream("abc"))

        Assert.assertEquals('a'.toInt(), stream.read())

        val index = stream.index

        Assert.assertEquals('b'.toInt(), stream.read())
        Assert.assertEquals('c'.toInt(), stream.read())
        Assert.assertEquals(-1, stream.read())

        stream.reset(index)

        Assert.assertEquals('b'.toInt(), stream.read())
        Assert.assertEquals('c'.toInt(), stream.read())
        Assert.assertEquals(-1, stream.read())
    }

    @Test
    fun testBufferTooSmall() {
        val stream = IndexedStream.of(StringCodePointStream("abcd"), 2)

        val startIndex = stream.index

        Assert.assertEquals('a'.toInt(), stream.read())
        Assert.assertEquals('b'.toInt(), stream.read())
        Assert.assertEquals('c'.toInt(), stream.read())

        try {
            stream.reset(startIndex)
            Assert.fail()
        } catch (e: OutOfBufferRangeException) {
            e.printStackTrace()
        }
    }

    @Test
    fun testBufferFullyUsed() {
        val stream = IndexedStream.of(StringCodePointStream("abcd"), 3)

        val startIndex = stream.index

        Assert.assertEquals('a'.toInt(), stream.read())
        Assert.assertEquals('b'.toInt(), stream.read())
        Assert.assertEquals('c'.toInt(), stream.read())

        stream.reset(startIndex)

        Assert.assertEquals('a'.toInt(), stream.read())
        Assert.assertEquals('b'.toInt(), stream.read())
        Assert.assertEquals('c'.toInt(), stream.read())
        Assert.assertEquals('d'.toInt(), stream.read())

        Assert.assertEquals(-1, stream.read())
    }

    @Test
    fun testSubstring() {
        val stream = IndexedStream.of(StringCodePointStream("abcd"), 3)

        Assert.assertEquals('a'.toInt(), stream.read())

        val startIndex = stream.index

        Assert.assertEquals('b'.toInt(), stream.read())
        Assert.assertEquals('c'.toInt(), stream.read())

        Assert.assertEquals("bc", stream.getString(startIndex, stream.index))

        Assert.assertEquals('d'.toInt(), stream.read())

        Assert.assertEquals("bcd", stream.getString(startIndex, stream.index))
    }
}