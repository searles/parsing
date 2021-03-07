package at.searles.parsing.parser

import at.searles.parsing.lexer.Lexer
import at.searles.parsing.lexer.regexp.CharSet
import at.searles.parsing.lexer.regexp.Text
import at.searles.parsing.parser.combinators.TokenParser
import at.searles.parsing.parser.combinators.TokenRecognizer
import at.searles.parsing.parser.combinators.ref
import at.searles.parsing.parser.tools.AsInt
import at.searles.parsing.parser.tools.AsString
import at.searles.parsing.parser.tools.ReducerBuilders.newInstance
import at.searles.parsing.parser.tools.ReducerBuilders.plus
import at.searles.parsing.ruleset.Grammar
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

    @Test
    fun testSwapPrint() {
        val lexer = Lexer()
        val a = TokenRecognizer.text("a", lexer).init(1)
        val b = TokenRecognizer.text("b", lexer).init(1)

        val ab = a.or(b, swapPrint = true)

        val printResult = ab.print(1)

        Assert.assertTrue(printResult.isSuccess)
        Assert.assertEquals("b", printResult.asString())
    }


    @Test
    fun testSwapPrintMultipleItems() {
        val lexer = Lexer()
        val a = TokenRecognizer.text("a", lexer).init(1)
        val b = TokenRecognizer.text("b", lexer).init(1)
        val c = TokenRecognizer.text("c", lexer).init(1)

        val ab = a.or(b, swapPrint = true).or(c, swapPrint = false)

        val printResult = ab.print(1)

        Assert.assertTrue(printResult.isSuccess)
        Assert.assertEquals("b", printResult.asString())
    }

    @Test
    fun testItextMany() {
        val rules = object: Grammar {
            override val lexer = Lexer().apply { createSpecialToken(Text(" ")) }
            val a = itext("a", "b")
        }

        val result = rules.a.parse(ParserStream("A B"))
        Assert.assertTrue(result.isSuccess)
        Assert.assertEquals("ab", rules.a.print().asString())
    }

    @Test
    fun testPlusStraightened() {
        val rules = object: Grammar {
            override val lexer = Lexer().apply { createSpecialToken(Text(" ")) }
            val a = itext("a").init(1)
            val b = itext("b").init(2)
            val c = itext("c").init(3)

            val abc = a + b + c
        }

        Assert.assertEquals(Pair(Pair(1, 2), 3), rules.abc.parse(ParserStream("abc")).value)
    }

    @Test
    fun testParsePersonExample() {
        class Person(val firstName: String, val lastName: String, val age: Int)

        val rules = object: Grammar {
            override val lexer = Lexer().apply { createSpecialToken(Text(" ")) }
            val name = rex(CharSet('A'..'Z', 'a'..'z').rep1()) + AsString
            val num = rex(CharSet('0'..'9').rep1()) + AsInt

            val person = name + name + text(",") + num + newInstance<Person>()
        }

        val result = rules.person.parse(ParserStream("Joe Biden, 78"))
        Assert.assertTrue(result.isSuccess)
        val person = result.value
        Assert.assertEquals("Joe", person.firstName)
        Assert.assertEquals("Biden", person.lastName)
        Assert.assertEquals(78, person.age)
    }

    @Suppress("unused")
    @Test
    fun testPrintPersonExample() {
        class Person(val firstName: String, val lastName: String, val age: Int)

        val rules = object: Grammar {
            override val lexer = Lexer().apply { createSpecialToken(Text(" ")) }
            val name = rex(CharSet('A'..'Z', 'a'..'z').rep1()) + AsString
            val num = rex(CharSet('0'..'9').rep1()) + AsInt

            val person = name + name + text(",") + num + newInstance<Person>()
        }

        val result = rules.person.print(Person("John", "Doe", 111))
        Assert.assertTrue(result.isSuccess)
        Assert.assertEquals("JohnDoe,111", result.asString())
    }

    @Test
    fun testReversePrint() {
        val rules = object: Grammar {
            override val lexer = Lexer()

            val a = text("a").init(1)
            val b = text("b").init(1)

            val aOrB = a.or(b, swapPrint = true)
        }

        Assert.assertEquals("b", rules.aOrB.print(1).asString())
    }

    @Test
    fun testDoNotReverseParseForReversePrint() {
        val rules = object: Grammar {
            override val lexer = Lexer()

            val a = text("a").init(1)
            val a2 = text("a").init(2)

            val aOrA2 = a.or(a2, swapPrint = true)
        }

        Assert.assertEquals(1, rules.aOrA2.parse(ParserStream("a")).value)
    }
}