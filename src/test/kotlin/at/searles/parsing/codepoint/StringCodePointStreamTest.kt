package at.searles.parsing.codepoint

import org.junit.Assert
import org.junit.Test

class StringCodePointStreamTest {
    @Test
    fun testCodePointIsEmpty() {
        val stream = StringCodePointStream("")
        Assert.assertEquals(-1, stream.read())
    }

    @Test
    fun testCodePointIsHasOneChar() {
        val stream = StringCodePointStream("a")
        Assert.assertEquals('a'.toInt(), stream.read())
        Assert.assertEquals(-1, stream.read())
    }

    @Test
    fun testCodePointHasOneUnicodeChar() {
        val stream = StringCodePointStream("\uD83C\uDF09")
        Assert.assertEquals(0x1F309, stream.read())
        Assert.assertEquals(-1, stream.read())
    }

    @Test
    fun testCodePointHasOneCharAndOneUnicodeChar() {
        val stream = StringCodePointStream("a\uD83C\uDF09")
        Assert.assertEquals('a'.toInt(), stream.read())
        Assert.assertEquals(0x1F309, stream.read())
        Assert.assertEquals(-1, stream.read())
    }

    @Test
    fun testBacktrackInStringStream() {
        val stream = StringCodePointStream("abc")
        Assert.assertEquals('a'.toInt(), stream.read())
        Assert.assertEquals('b'.toInt(), stream.read())
        stream.backtrackToIndex(0)
        Assert.assertEquals('a'.toInt(), stream.read())
    }

    @Test
    fun testSubstring() {
        val stream = StringCodePointStream("abcd")

        Assert.assertEquals('a'.toInt(), stream.read())

        val startIndex = stream.index

        Assert.assertEquals('b'.toInt(), stream.read())
        Assert.assertEquals('c'.toInt(), stream.read())

        val length = stream.index - startIndex

        Assert.assertEquals("bc", stream.getString(startIndex, length.toInt()))

        Assert.assertEquals('d'.toInt(), stream.read())

        Assert.assertEquals("bc", stream.getString(startIndex, length.toInt()))
    }

    @Test
    fun testCodePointAt() {
        val stream = StringCodePointStream("A\uD83C\uDF09B")

        Assert.assertEquals('A'.toInt(), stream.getCodePointAt(0))

        Assert.assertEquals(0x1F309, stream.getCodePointAt(1))
        Assert.assertEquals(-1, stream.getCodePointAt(2))
        Assert.assertEquals('B'.toInt(), stream.getCodePointAt(3))
    }
}