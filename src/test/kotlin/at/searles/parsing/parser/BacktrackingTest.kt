package at.searles.parsing.parser

import at.searles.parsing.lexer.Lexer
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

        val createString = object: Conversion<CharSequence, String> {
            override fun convert(left: CharSequence): String {
                return left.toString()
            }
        }

        val appendString = object: Fold<String, String, String> {
            override fun fold(left: String, right: String): String {
                return left + right
            }
        }

        val abParser = TokenParser(tokenA) + createString + (TokenParser(tokenB) + createString + appendString)
        val acParser = TokenParser(tokenA) + createString + (TokenParser(tokenC) + createString + appendString)

        val abacParser = abParser or acParser

        val result = abacParser.parse(ParserStream("ac"))

        Assert.assertTrue(result.isSuccess)
    }

    private fun bbbParser(): Reducer<String, String> {
        val lexer = Lexer()

        val tokenB = lexer.createToken(Text("b"))

        val createString = object : Conversion<CharSequence, String> {
            override fun convert(left: CharSequence): String {
                return left.toString()
            }
        }

        val appendString = object : Fold<String, String, String> {
            override fun fold(left: String, right: String): String {
                return left + right
            }
        }

        return (TokenParser(tokenB) + createString + appendString).rep(3)
    }

    @Test
    fun repNSuccess() {
        val bbbParser = bbbParser()
        val result = bbbParser.parse(ParserStream("bbb"), "")

        Assert.assertTrue(result.isSuccess)
        Assert.assertEquals("bbb", result.value)
        Assert.assertEquals(3, result.length)
        Assert.assertEquals(0, result.index)
    }


    @Test
    fun repNFail() {
        val bbbParser = bbbParser()
        val result = bbbParser.parse(ParserStream("bb"), "")

        Assert.assertTrue(!result.isSuccess)
    }
}