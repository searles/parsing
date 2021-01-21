package at.searles.parsing.parser

import at.searles.parsing.lexer.Lexer
import at.searles.parsing.lexer.regexp.CharSet
import at.searles.parsing.parser.combinators.ParserPlusReducer
import at.searles.parsing.parser.combinators.TokenParser
import at.searles.parsing.parser.combinators.TokenRecognizer
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.lang.Exception

class ParserCombinatorTest {
    private lateinit var lexer: Lexer
    private lateinit var number: Parser<CharSequence>
    private lateinit var toInt: Conversion<CharSequence, Int>
    private lateinit var plusSign: Recognizer
    private lateinit var intParser: Parser<Int>
    private lateinit var additionOp: Fold<Int, Int, Int>
    private lateinit var addition: Parser<Int>

    @Before
    fun setUp() {
        lexer = Lexer()
        number = TokenParser(lexer.createToken(CharSet('0'..'9').rep1()))
        toInt = object: Conversion<CharSequence, Int> {
            override fun convert(value: CharSequence): Int {
                return value.toString().toInt()
            }
        }
        plusSign = TokenRecognizer.text("+", lexer)
        intParser = number + toInt

        additionOp = object: Fold<Int, Int, Int> {
            override fun fold(left: Int, right: Int): Int {
                return left + right
            }
        }

        addition = intParser + (plusSign + intParser + additionOp)
    }

    @Test
    fun testSuccessParserWithMapping() {
        val result = ParserPlusReducer(number, toInt).parse(ParserStream(("32")))

        Assert.assertTrue(result.isSuccess)
        Assert.assertEquals(32, result.value)
    }

    @Test
    fun testFailParserWithMapping() {
        val result = ParserPlusReducer(number, toInt).parse(ParserStream(("A")))

        Assert.assertFalse(result.isSuccess)
        try {
            result.value
            Assert.fail()
        } catch(e: Exception) {
            e.printStackTrace()
        }
    }

    @Test
    fun testAddition() {
        val result = addition.parse(ParserStream(("32+16")))

        Assert.assertTrue(result.isSuccess)
        Assert.assertEquals(32+16, result.value)
    }

    @Test
    fun testAdditionWithSpaces() {
        lexer.createSpecialToken(CharSet(' ', '\n', '\r', '\t').rep1())

        val result = addition.parse(ParserStream(("32 + 16")))

        Assert.assertTrue(result.isSuccess)
        Assert.assertEquals(32+16, result.value)
    }

    @Test
    fun testAdditionWithSpacesPosition() {
        lexer.createSpecialToken(CharSet(' ', '\n', '\r', '\t').rep1())

        val result = addition.parse(ParserStream((" 32 + 16 ")))

        Assert.assertEquals(1, result.index)
        Assert.assertEquals(7, result.length)
    }
}