package at.searles.parsing

import at.searles.parsing.codepoint.FrameStream
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
        val stream = FrameStream("07")

        val first = lexer.selectNextToken(stream)!!
        val firstMatch = stream.frame.toString()
        stream.next()

        val second = lexer.selectNextToken(stream)!!
        val secondMatch = stream.frame.toString()
        stream.next()

        val third = lexer.selectNextToken(stream)

        Assert.assertEquals("0", firstMatch)
        Assert.assertEquals("7", secondMatch)
        Assert.assertTrue(first.size == 1 && first.contains(zero))
        Assert.assertTrue(second.size == 1 && second.contains(seven))
        Assert.assertNull(third)
    }
}