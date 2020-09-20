package at.searles.regexp

import at.searles.buf.StringWrapper
import at.searles.lexer.Lexer
import org.junit.Assert
import org.junit.Test

class TextTest {
    @Test
    fun testCaseInsensitive() {
        val rex = Text.caseInsensitive("hello")
        val lexer = Lexer().apply { add(rex) }
        Assert.assertNotNull(lexer.readNextToken(StringWrapper("hElLo")))
    }
}