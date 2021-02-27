package at.searles.parsing.parser

import at.searles.parsing.lexer.Lexer
import at.searles.parsing.lexer.regexp.Text
import org.junit.Assert
import org.junit.Test
import java.io.StringReader

class ParserStreamTest {
    @Test
    fun testCreateFromString() {
        val stream = ParserStream("helloWorld")
        helloWorldTest(stream)
    }

    private fun helloWorldTest(stream: ParserStream) {
        val lexer = Lexer()

        val helloToken = lexer.createToken(Text("hello"))
        val worldToken = lexer.createToken(Text("World"))

        Assert.assertFalse(stream.parseToken(worldToken).isSuccess)
        Assert.assertTrue(stream.parseToken(helloToken).isSuccess)

        Assert.assertFalse(stream.parseToken(helloToken).isSuccess)
        Assert.assertTrue(stream.parseToken(worldToken).isSuccess)
    }

    @Test
    fun testCreateFromReader() {
        val stream = ParserStream(StringReader("helloWorld"))
        helloWorldTest(stream)
    }
}