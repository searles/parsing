package at.searles.parsing.ruleset

import at.searles.parsing.lexer.Lexer
import at.searles.parsing.lexer.regexp.CharSet
import at.searles.parsing.lexer.TokenStream
import org.junit.Assert
import org.junit.Test

class RexTest {
    @Test
    fun testRexWithConversion() {
        val rules = object: Grammar {
            override val lexer: Lexer = Lexer()
            val num = rex(CharSet('0' .. '9').rep1()) { it.toString().toInt() }
        }

        val parserResult = rules.num.parse(TokenStream("12"))
        val printResult = rules.num.print(24)

        Assert.assertTrue(parserResult.isSuccess)
        Assert.assertTrue(printResult.isSuccess)

        Assert.assertEquals(12, parserResult.value)
        Assert.assertEquals("24", printResult.asString())
    }
}