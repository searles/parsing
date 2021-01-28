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

    fun itext(vararg text: String): Recognizer {
        return text.map<String, Recognizer> {
            TokenRecognizer(lexer.createToken(Text.itext(it)), it)
        }.reduce { a, b -> a + b }
    }

    fun text(vararg text: String): Recognizer {
        return text.map<String, Recognizer> {
            TokenRecognizer(lexer.createToken(Text(it)), it)
        }.reduce { a, b -> a + b }
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