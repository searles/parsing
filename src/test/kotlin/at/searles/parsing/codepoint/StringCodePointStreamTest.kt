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
        stream.reset(0)
        Assert.assertEquals('a'.toInt(), stream.read())
    }

    @Test
    fun testSubstring() {
        val stream = StringCodePointStream("abcd")

        Assert.assertEquals('a'.toInt(), stream.read())

        val startIndex = stream.index

        Assert.assertEquals('b'.toInt(), stream.read())
        Assert.assertEquals('c'.toInt(), stream.read())

        val endIndex = stream.index

        Assert.assertEquals("bc", stream.getString(startIndex, endIndex))

        Assert.assertEquals('d'.toInt(), stream.read())

        Assert.assertEquals("bc", stream.getString(startIndex, endIndex))
    }

    @Test
    fun testCodePointAt() {
        val stream = StringCodePointStream("A\uD83C\uDF09B")

        Assert.assertEquals('A'.toInt(), stream.read())
        Assert.assertEquals(0x1F309, stream.read())
        Assert.assertEquals('B'.toInt(), stream.read())
        Assert.assertEquals(-1, stream.read())
    }
}