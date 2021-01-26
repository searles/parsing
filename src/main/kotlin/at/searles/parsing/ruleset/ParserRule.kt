package at.searles.parsing.ruleset

import at.searles.parsing.lexer.Lexer
import at.searles.parsing.lexer.regexp.CharSet
import at.searles.parsing.lexer.regexp.Regexp
import at.searles.parsing.lexer.regexp.Text
import at.searles.parsing.parser.Conversion
import at.searles.parsing.parser.FnResult
import at.searles.parsing.parser.Parser
import at.searles.parsing.parser.Recognizer
import at.searles.parsing.parser.combinators.TokenParser
import at.searles.parsing.parser.combinators.TokenRecognizer

interface ParserRules {
    val lexer: Lexer

    fun itext(text: String): Recognizer {
        val token = lexer.createToken(Text.itext(text))
        return TokenRecognizer(token, text)
    }

    fun text(text: String): Recognizer {
        val token = lexer.createToken(Text(text))
        return TokenRecognizer(token, text)
    }

    fun ch(char: Char): Recognizer {
        val token = lexer.createToken(CharSet(char))
        return TokenRecognizer(token, char.toString())
    }

    fun rex(regexp: Regexp): Parser<CharSequence> {
        return TokenParser(lexer.createToken(regexp))
    }

    fun <A> rex(regexp: Regexp, create: (CharSequence) -> A): Parser<A> {
        return TokenParser(lexer.createToken(regexp)) + object: Conversion<CharSequence, A> {
            override fun convert(value: CharSequence): A {
                return create(value)
            }

            override fun invert(value: A): FnResult<CharSequence> {
                return FnResult.success(value.toString())
            }
        }
    }
}