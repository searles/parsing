package at.searles.lexer

import at.searles.regexp.CharSet.Companion.chars
import at.searles.regexp.Text
import org.junit.Assert
import org.junit.Test

class TokenTest {
    @Test
    fun testCorrectHandlingMultipleLexems() {
        val lexer = Lexer()
        val tok1 = lexer.add(Text("="))
        val tok2 = lexer.add(Text("="))
        Assert.assertEquals(tok1.toLong(), tok2.toLong())
    }

    @Test
    fun testHiddenTokens() {
        val lexer = Lexer()
        val tokIf = lexer.add(Text("if"))
        val tokId = lexer.add(chars('a', 'z').rep1())
        val tokLe = lexer.add(Text("=<"))
        val tokEq = lexer.add(Text("=="))
        val tokGe = lexer.add(Text(">="))
        val tokAssign = lexer.add(Text("="))
    }
}