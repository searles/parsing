package at.searles.parsing.parser

import at.searles.parsing.lexer.Lexer
import at.searles.parsing.lexer.regexp.Text
import org.junit.Assert
import org.junit.Test
import java.io.StringReader
import java.lang.StringBuilder

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

    @Test
    fun filterSpecialTokens() {
        val lexer = Lexer()

        lexer.createToken(Text("A"))
        lexer.createSpecialToken(Text("1"))

        val stream = ParserStream("1A1A1")

        val sb = StringBuilder()

        while(stream.fetchNextToken(lexer)) {
            sb.append(stream.getFrame())
        }

        Assert.assertEquals("AA", sb.toString())
    }
}