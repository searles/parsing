package at.searles.parsing.parser

import at.searles.parsing.lexer.Lexer
import at.searles.parsing.lexer.regexp.CharSet
import at.searles.parsing.parser.combinators.TokenParser
import at.searles.parsing.parser.combinators.TokenRecognizer
import at.searles.parsing.parser.combinators.ref
import org.junit.Assert
import org.junit.Test

class ParserTest {
    @Test
    fun testNullAsParserResult() {
        val lexer = Lexer()

        val mapping = object: Conversion<CharSequence, Int?> {
            override fun convert(value: CharSequence): Int? {
                return when(value[0]) {
                    'a' -> 0
                    'b' -> null
                    else -> error("not possible")
                }
            }

            override fun invert(value: Int?): FnResult<CharSequence> {
                return when(value) {
                    0 -> FnResult.success("a")
                    null -> FnResult.success("b")
                    else -> FnResult.failure
                }
            }
        }

        val parser = TokenParser(lexer.createToken(CharSet('a', 'b'))) + mapping

        val aResult = parser.parse(ParserStream("a"))
        val bResult = parser.parse(ParserStream("b"))

        Assert.assertTrue(aResult.isSuccess)
        Assert.assertTrue(bResult.isSuccess)

        Assert.assertEquals(0, aResult.value)
        Assert.assertEquals(null, bResult.value)

        val zeroPrinted = parser.print(0)
        val nullPrinted = parser.print(null)

        Assert.assertTrue(zeroPrinted.isSuccess)
        Assert.assertTrue(nullPrinted.isSuccess)

        Assert.assertEquals("a", zeroPrinted.asString())
        Assert.assertEquals("b", nullPrinted.asString())
    }

    @Test
    fun testListParser() {
        val lexer = Lexer()

        val createString = object: Conversion<CharSequence, String> {
            override fun convert(value: CharSequence): String {
                return value.toString()
            }
        }

        val parser = TokenParser(lexer.createToken(CharSet('a'..'z'))) + createString

        val listParser = parser.rep()

        val result = listParser.parse(ParserStream("abc"))

        Assert.assertTrue(result.isSuccess)
        Assert.assertEquals(listOf("a", "b", "c"), result.value)
    }

    @Test
    fun testOptParser() {
        val lexer = Lexer()
        val parser = TokenParser(lexer.createToken(CharSet('a'..'z'))).opt()

        val result = parser.parse(ParserStream("1"))

        Assert.assertTrue(result.isSuccess)
        Assert.assertNull(result.value)
    }

    @Test
    fun testPairParser() {
        val lexer = Lexer()
        val parser = TokenParser(lexer.createToken(CharSet('a'..'z'))).opt()

        val pairParser = parser + parser

        val result = pairParser.parse(ParserStream("ab"))

        Assert.assertTrue(result.isSuccess)
        Assert.assertEquals(Pair("a", "b"), result.value)
    }


    @Test
    fun testSelfReferringParser() {
        val str = object: Conversion<CharSequence, String> {
            override fun convert(value: CharSequence): String {
                return value.toString()
            }
        }

        val stringAppend = object: Fold<String, String, String> {
            override fun fold(left: String, right: String): String {
                return left + right
            }
        }

        val parserSet = object {
            val recursiveParser: Parser<String> by ref { charParser + (recursiveParser + stringAppend) or charParser }
            val charParser = TokenParser(Lexer().createToken(CharSet('a'..'z'))) + str
        }

        val result = parserSet.recursiveParser.parse(ParserStream("abcde"))

        Assert.assertTrue(result.isSuccess)
        Assert.assertEquals("abcde", result.value)
    }

    @Test
    fun testFlag() {
        val lexer = Lexer()
        val a = TokenRecognizer.text("a", lexer).flag()

        val resultTrue = a.parse(ParserStream("a"))
        val resultFalse = a.parse(ParserStream(""))

        Assert.assertTrue(resultTrue.isSuccess)
        Assert.assertTrue(resultFalse.isSuccess)

        Assert.assertTrue(resultTrue.value)
        Assert.assertFalse(resultFalse.value)
    }
}