package at.searles.parsing.parser

import at.searles.parsing.lexer.Frame
import at.searles.parsing.lexer.Lexer
import at.searles.parsing.lexer.regexp.CharSet
import at.searles.parsing.lexer.regexp.Text
import at.searles.parsing.parser.combinators.ParserPlusReducer
import at.searles.parsing.parser.combinators.TokenParser
import at.searles.parsing.parser.combinators.TokenRecognizer
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.lang.Exception

class ParserCombinatorTest {

    lateinit var lexer: Lexer
    lateinit var number: Parser<Frame>
    lateinit var toInt: Conversion<Frame, Int>

    @Before
    fun setUp() {
        lexer = Lexer()
        number = TokenParser(lexer.createToken(CharSet.interval('0'..'9').rep1()))
        toInt = object: Conversion<Frame, Int> {
            override fun convert(left: Frame): Int {
                return left.string.toInt()
            }
        }
    }

    @Test
    fun testSuccessParserWithMapping() {
        val parserResult = ParserPlusReducer(number, toInt).parse(ParserStream(("32")))

        Assert.assertTrue(parserResult.isSuccess)
        Assert.assertEquals(32, parserResult.value)
    }

    @Test
    fun testFailParserWithMapping() {
        val parserResult = ParserPlusReducer(number, toInt).parse(ParserStream(("A")))

        Assert.assertFalse(parserResult.isSuccess)
        try {
            parserResult.value
            Assert.fail()
        } catch(e: Exception) {
            e.printStackTrace()
        }
    }

    @Test
    fun testAddition() {
        val plusSign = TokenRecognizer(lexer.createToken(Text("+")))
        val intParser = number + toInt

        val additionOp = object: Fold<Int, Int, Int> {
            override fun fold(left: Int, right: Int): Int {
                return left + right
            }
        }

        val addition = intParser + (plusSign + intParser + additionOp)


        val parserResult = addition.parse(ParserStream(("32+16")))

        Assert.assertTrue(parserResult.isSuccess)
        Assert.assertEquals(32+16, parserResult.value)
    }
}