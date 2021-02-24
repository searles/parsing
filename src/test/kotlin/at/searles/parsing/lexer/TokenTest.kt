package at.searles.parsing.lexer

import at.searles.parsing.codepoint.StringCodePointStream
import at.searles.parsing.lexer.regexp.Text
import org.junit.Assert
import org.junit.Test

class TokenTest {
    @Test
    fun testSameLexemsAreEqual() {
        val lexer = Lexer()
        val tok1 = lexer.createToken(Text("="))
        val tok2 = lexer.createToken(Text("="))
        Assert.assertEquals(tok1.tokenId, tok2.tokenId)
    }

    @Test
    fun testCanCreateSpecialToken() {
        val lexer = Lexer()
        val tokenId = lexer.createSpecialToken(Text("="))

        val fs = FrameStream(StringCodePointStream("="))

        val tokenMatcher = lexer.readNextToken(fs)

// TODO Proposal for improvement       Assert.assertTrue(tokenMatcher.matches(tokenId))
//        Assert.assertTrue(tokenMatcher.hasSpecialToken())
    }
}