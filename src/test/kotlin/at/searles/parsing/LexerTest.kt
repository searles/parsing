package at.searles.parsing

import at.searles.parsing.codepoint.StringCodePointStream
import at.searles.parsing.lexer.FrameStream
import at.searles.parsing.lexer.Lexer
import at.searles.parsing.lexer.regexp.Text
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class LexerTest {
    private lateinit var lexer: Lexer

    @Before
    fun setUp() {
        lexer = Lexer()
    }

    @Test
    fun testSimpleLexer() {
        val zero = lexer.createToken(Text("0"))
        val seven = lexer.createToken(Text("7"))

        val stream = FrameStream(StringCodePointStream("07"))

        val first = lexer.readNextToken(stream)!!
        Assert.assertEquals("0", stream.frame.toString())
        val second = lexer.readNextToken(stream)!!
        Assert.assertEquals("7", stream.frame.toString())
        val third = lexer.readNextToken(stream)
        Assert.assertEquals("", stream.frame.toString())

        Assert.assertTrue(first.size == 1 && first.contains(zero.tokenId))
        Assert.assertTrue(second.size == 1 && second.contains(seven.tokenId))
        Assert.assertNull(third)
    }
}