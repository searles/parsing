package at.searles.parsing.parser

import at.searles.parsing.codepoint.Frame
import at.searles.parsing.lexer.Lexer
import at.searles.parsing.lexer.TokenStream
import at.searles.parsing.lexer.regexp.Text
import at.searles.parsing.parser.Reducer.Companion.rep
import at.searles.parsing.parser.combinators.TokenParser
import org.junit.Assert
import org.junit.Test

class BacktrackingTest {
    @Test
    fun simpleChoice() {
        val lexer = Lexer()

        val tokenA = lexer.createToken(Text("a"))
        val tokenB = lexer.createToken(Text("b"))
        val tokenC = lexer.createToken(Text("c"))

        val createString = Conversion<CharSequence, String> { value -> value.toString() }

        val appendString = Fold<String, String, String> { left, right -> left + right }

        val abParser = TokenParser(tokenA, lexer, createString) + (TokenParser(tokenB, lexer, createString) + appendString)
        val acParser = TokenParser(tokenA, lexer, createString) + (TokenParser(tokenC, lexer, createString) + appendString)

        val abacParser = abParser or acParser

        val result = abacParser.parse(TokenStream("ac"))

        Assert.assertTrue(result.isSuccess)
    }

    private fun bbbParser(): Reducer<String, String> {
        val lexer = Lexer()

        val tokenB = lexer.createToken(Text("b"))

        val createString = Conversion<CharSequence, String> { value -> value.toString() }

        val appendString = Fold<String, String, String> { left, right -> left + right }

        return (TokenParser(tokenB, lexer, createString) + appendString).rep(3)
    }

    @Test
    fun repNSuccess() {
        val bbbParser = bbbParser()
        val result = bbbParser.parse(TokenStream("bbb"), "")

        Assert.assertTrue(result.isSuccess)
        Assert.assertEquals("bbb", result.value)
        Assert.assertEquals(3, result.endIndex)
        Assert.assertEquals(0, result.startIndex)
    }


    @Test
    fun repNFail() {
        val bbbParser = bbbParser()
        val result = bbbParser.parse(TokenStream("bb"), "")

        Assert.assertTrue(!result.isSuccess)
    }
}