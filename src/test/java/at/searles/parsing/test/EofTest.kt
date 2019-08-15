package at.searles.parsing.test

import at.searles.lexer.Lexer
import at.searles.lexer.LexerWithHidden
import at.searles.parsing.ParserCallBack
import at.searles.parsing.ParserStream
import at.searles.parsing.Recognizable
import at.searles.parsing.Recognizer
import at.searles.regex.CharSet
import org.junit.Assert
import org.junit.Test

class EofTest {

    private lateinit var parser: Recognizable
    private lateinit var eof: Recognizer
    private lateinit var env: ParserCallBack
    private lateinit var stream: ParserStream

    @Test
    fun testEof() {
        // Set up phase
        val lexer = Lexer()
        this.env = ParserCallBack { _, _ -> }
        this.parser = Recognizer.fromString("a", lexer, false).rep()
        eof = Recognizer.eof(lexer)

        withInput("aaa")
        actRecognize()

        Assert.assertTrue(eof())
    }

    @Test
    fun testNoEof() {
        // Set up phase
        val lexer = Lexer()
        this.env = ParserCallBack { _, _ -> }
        val b = Recognizer.fromString("b", lexer, false)
        this.parser = Recognizer.fromString("a", lexer, false).rep()
        eof = Recognizer.eof(lexer)

        withInput("aaab")
        actRecognize()

        Assert.assertFalse(eof())
    }

    @Test
    fun testNoEofOtherLexer() {
        // Set up phase
        val lexer = Lexer()
        this.env = ParserCallBack { _, _ -> }
        val b = Recognizer.fromString("b", lexer, false)
        this.parser = Recognizer.fromString("a", lexer, false).rep()
        eof = Recognizer.eof(Lexer())

        withInput("aaab")
        actRecognize()

        Assert.assertFalse(eof())
    }

    @Test
    fun testEofWithHidden() {
        // Set up phase
        val lexer = LexerWithHidden()
        lexer.hiddenToken(CharSet.chars(' '.toInt()))
        this.env = ParserCallBack { _, _ -> }
        this.parser = Recognizer.fromString("a", lexer, false).rep()
        eof = Recognizer.eof(lexer)

        withInput("a a a   ")
        actRecognize()

        Assert.assertTrue(eof())
    }


    @Test
    fun testEofWithHiddenAndSeparateLexer() {
        // Set up phase
        val lexer = LexerWithHidden()
        lexer.hiddenToken(CharSet.chars(' '.toInt()))
        this.env = ParserCallBack { _, _ -> }
        this.parser = Recognizer.fromString("a", lexer, false).rep()
        eof = Recognizer.eof(Lexer())

        withInput("a a a   ")
        actRecognize()

        Assert.assertTrue(eof())
    }

    private fun eof(): Boolean {
        return eof.recognize(env, stream)
    }

    private fun actRecognize() {
        parser.recognize(env, stream)
    }

    private fun withInput(input: String) {
        stream = ParserStream.fromString(input)
    }
}
