package at.searles.parsing

import at.searles.lexer.Lexer
import at.searles.lexer.SkipTokenizer
import at.searles.parsing.Reducer.Companion.opt
import at.searles.parsingtools.common.Init
import at.searles.parsingtools.common.StringAppender
import at.searles.parsingtools.common.ToString
import at.searles.regexp.Text
import org.junit.Assert
import org.junit.Test

class LazyParsingTest {
    @Test
    fun testOrExpansionInParser() {
        // Set up phase
        val lexer = Lexer()
        val tokenizer = SkipTokenizer(lexer)

        val a = Parser.fromRegex(Text("a"), tokenizer, ToString)
        val c = Parser.fromRegex(Text("c"), tokenizer, ToString)

        val cAppend = c + StringAppender

        val parser = (a + cAppend or a) + c // only second or-branch is matching in total.

        val stream = ParserStream.create("ac")

        val ast = parser.parse(stream)

        Assert.assertNotNull(ast)
    }

    @Test
    fun testOrExpansionInReducer() {
        // Set up phase
        val lexer = Lexer()
        val tokenizer = SkipTokenizer(lexer)

        val a = Parser.fromRegex(Text("a"), tokenizer, ToString) + StringAppender
        val c = Parser.fromRegex(Text("c"), tokenizer, ToString) + StringAppender

        val parser = Init("") + (a + c or a) + c // only second or-branch is matching in total.

        val stream = ParserStream.create("ac")

        val ast = parser.parse(stream)

        Assert.assertNotNull(ast)
    }

    @Test
    fun testOptExpansion() {
        // Set up phase
        val lexer = Lexer()
        val tokenizer = SkipTokenizer(lexer)

        val a = Parser.fromRegex(Text("a"), tokenizer, ToString)
        val c = Parser.fromRegex(Text("c"), tokenizer, ToString)

        val parser = (a + (c + StringAppender).opt()) + c

        val stream = ParserStream.create("ac")

        val ast = parser.parse(stream)

        Assert.assertNotNull(ast)
    }
}