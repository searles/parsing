package at.searles.parsing.parser

import at.searles.parsing.lexer.Lexer
import at.searles.parsing.parser.combinators.TokenRecognizer
import at.searles.parsing.parser.combinators.ref
import org.junit.Assert
import org.junit.Test

class RefParserTest {
    // label is important for debugging
    val parserUnlabeled: Parser<String> by ref {
        TokenRecognizer.text("Hello", Lexer()).init("Hello")
    }

    // label is important for debugging
    val parserLabeled: Parser<String> by ref("label") {
        TokenRecognizer.text("Hello", Lexer()).init("Hello")
    }

    @Test
    fun testLabel() {
        Assert.assertEquals("label", parserLabeled.toString())
        Assert.assertEquals("'Hello'.plus({Hello})", parserUnlabeled.toString())
    }
}