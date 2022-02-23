package at.searles.parsing.parser

import at.searles.parsing.codepoint.Frame
import at.searles.parsing.lexer.Lexer
import at.searles.parsing.lexer.TokenStream
import at.searles.parsing.lexer.regexp.CharSet
import at.searles.parsing.parser.combinators.TokenParser
import at.searles.parsing.parser.combinators.TokenRecognizer
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.lang.Exception

class ParserCombinatorTest {
    private lateinit var lexer: Lexer
    private lateinit var number: Parser<Int>
    private lateinit var plusSign: Recognizer
    private lateinit var additionOp: Fold<Int, Int, Int>
    private lateinit var addition: Parser<Int>

    @Before
    fun setUp() {
        lexer = Lexer()
        number = TokenParser(lexer.createToken(CharSet('0'..'9').rep1()), lexer) {
            (it as Frame).fold(0) { l, r -> l * 10 + r - '0'.toInt() }
        }
        plusSign = TokenRecognizer.text("+", lexer)

        additionOp = Fold { left, right -> left + right }

        addition = number + (plusSign + number + additionOp)
    }

    @Test
    fun testSuccessParserWithMapping() {
        val result = number.parse(TokenStream(("32")))

        Assert.assertTrue(result.isSuccess)
        Assert.assertEquals(32, result.value)
    }

    @Test
    fun testFailParserWithMapping() {
        val result = number.parse(TokenStream(("A")))

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
        val result = addition.parse(TokenStream(("32+16")))

        Assert.assertTrue(result.isSuccess)
        Assert.assertEquals(32+16, result.value)
    }

    // TODO
//    @Test
//    fun testAdditionWithSpaces() {
//        lexer.createSpecialToken(CharSet(' ', '\n', '\r', '\t').rep1())
//
//        val result = addition.parse(TokenStream(("32 + 16")))
//
//        Assert.assertTrue(result.isSuccess)
//        Assert.assertEquals(32+16, result.value)
//    }
//
//    @Test
//    fun testAdditionWithSpacesPosition() {
//        lexer.createSpecialToken(CharSet(' ', '\n', '\r', '\t').rep1())
//
//        val result = addition.parse(TokenStream((" 32 + 16 ")))
//
//        Assert.assertEquals(1, result.index)
//        Assert.assertEquals(7, result.length)
//    }

}