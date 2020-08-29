package at.searles.regexp

import at.searles.lexer.Lexer
import at.searles.parsing.ParserStream
import at.searles.parsing.grammar.Grammar
import org.junit.Assert
import org.junit.Test

class GrammarTest {
    @Test
    fun testItextLL() {
        val grammar = Grammar(Lexer())
        val azRecognizer = grammar.itext("az")

        val stream = ParserStream.create("az")
        Assert.assertTrue(azRecognizer.recognize(stream))
    }

    @Test
    fun testItextUU() {
        val grammar = Grammar(Lexer())
        val azRecognizer = grammar.itext("az")

        val stream = ParserStream.create("AZ")
        Assert.assertTrue(azRecognizer.recognize(stream))
    }

    @Test
    fun testItextLU() {
        val grammar = Grammar(Lexer())
        val azRecognizer = grammar.itext("az")

        val stream = ParserStream.create("aZ")
        Assert.assertTrue(azRecognizer.recognize(stream))
    }

    @Test
    fun testItextUL() {
        val grammar = Grammar(Lexer())
        val azRecognizer = grammar.itext("az")

        val stream = ParserStream.create("AZ")
        Assert.assertTrue(azRecognizer.recognize(stream))
    }

    @Test
    fun testItextFail() {
        val grammar = Grammar(Lexer())
        val azRecognizer = grammar.itext("az")

        val stream = ParserStream.create("Ab")
        Assert.assertFalse(azRecognizer.recognize(stream))
    }
}