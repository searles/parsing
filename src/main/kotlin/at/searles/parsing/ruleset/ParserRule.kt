package at.searles.parsing.ruleset

import at.searles.parsing.lexer.FrameStream
import at.searles.parsing.lexer.Lexer
import at.searles.parsing.lexer.regexp.Regexp
import at.searles.parsing.parser.Parser
import at.searles.parsing.parser.Recognizer
import at.searles.parsing.parser.combinators.TokenParser
import at.searles.parsing.parser.combinators.TokenRecognizer

interface ParserRule<T>: Parser<T> {
    val lexer: Lexer

    fun text(text: String): Recognizer {
        return TokenRecognizer.text(text, lexer)
    }

    fun rex(regexp: Regexp): Parser<CharSequence> {
        return TokenParser(lexer.createToken(regexp))
    }
}