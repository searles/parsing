package at.searles.parsing.lexer

import at.searles.parsing.lexer.regexp.Text
import org.junit.Assert
import org.junit.Test

class TokenTest {
    @Test
    fun testSameLexemsAreEqual() {
        val lexer = Lexer()
        val tok1 = lexer.add(Text("="))
        val tok2 = lexer.add(Text("="))
        Assert.assertEquals(tok1.toLong(), tok2.toLong())
    }
}