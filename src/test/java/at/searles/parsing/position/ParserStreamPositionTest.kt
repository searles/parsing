package at.searles.parsing.position

import at.searles.lexer.Lexer
import at.searles.parsing.Parser
import at.searles.parsing.ParserStream
import at.searles.parsing.Recognizer
import at.searles.parsingtools.common.ToString
import at.searles.regexp.Text
import org.junit.Assert
import org.junit.Test

class ParserStreamPositionTest {
    @Test
    fun testRecognizerThenParser() {
        val lexer = Lexer()

        val a = Recognizer.fromString("a", lexer)
        val b = Parser.fromRegex(Text("b"), lexer, ToString)

        val ab = a + b

        val stream = ParserStream.create("ab")
        val parsed = stream.parse(ab)

        Assert.assertEquals("b", parsed)

        Assert.assertEquals(0, stream.start)
        Assert.assertEquals(2, stream.end)
    }

    @Test
    fun testRecognizerThenRecognizer() {
        val lexer = Lexer()

        val a = Recognizer.fromString("a", lexer)
        val b = Recognizer.fromString("b", lexer)

        val ab = a + b

        val stream = ParserStream.create("ab")
        val status = ab.recognize(stream)

        Assert.assertTrue(status)

        Assert.assertEquals(0, stream.start)
        Assert.assertEquals(2, stream.end)
    }
}