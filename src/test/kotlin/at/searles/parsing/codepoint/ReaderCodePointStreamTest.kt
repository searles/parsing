package at.searles.parsing.codepoint

import org.junit.Assert
import org.junit.Test
import java.io.StringReader

class ReaderCodePointStreamTest {
    @Test
    fun testCodePointIsEmpty() {
        val stream = ReaderCodePointStream(StringReader(""))
        Assert.assertEquals(-1, stream.read())
    }

    @Test
    fun testCodePointIsHasOneChar() {
        val stream = ReaderCodePointStream(StringReader("a"))
        Assert.assertEquals('a'.toInt(), stream.read())
        Assert.assertEquals(-1, stream.read())
    }

    @Test
    fun testCodePointHasOneUnicodeChar() {
        val stream = ReaderCodePointStream(StringReader("\uD83C\uDF09"))
        Assert.assertEquals(0x1F309, stream.read())
        Assert.assertEquals(-1, stream.read())
    }

    @Test
    fun testCodePointHasOneCharAndOneUnicodeChar() {
        val stream = ReaderCodePointStream(StringReader("a\uD83C\uDF09"))
        Assert.assertEquals('a'.toInt(), stream.read())
        Assert.assertEquals(0x1F309, stream.read())
        Assert.assertEquals(-1, stream.read())
    }
}