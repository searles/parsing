package at.searles.parsing.ruleset

import at.searles.parsing.lexer.Lexer
import at.searles.parsing.lexer.regexp.CharSet
import at.searles.parsing.lexer.regexp.Regexp
import at.searles.parsing.lexer.regexp.Text
import at.searles.parsing.parser.*
import at.searles.parsing.parser.combinators.TokenParser
import at.searles.parsing.parser.combinators.TokenRecognizer

interface Grammar {
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

    fun rex(regexpString: String): Parser<CharSequence> {
        val regexp by RegexpGrammar.regexp.parse(ParserStream(regexpString))

        return TokenParser(lexer.createToken(regexp))
    }

    val eof get() = TokenRecognizer(lexer.createToken(CharSet.eof()), "")

    fun <A> rex(regexp: Regexp, create: (CharSequence) -> A): Parser<A> {
        return TokenParser(lexer.createToken(regexp)) + CreateFromString(create)
    }

    class CreateFromString<A>(val create: (CharSequence) -> A): Conversion<CharSequence, A> {
        override fun convert(value: CharSequence): A {
            return create(value)
        }

        override fun invert(value: A): FnResult<CharSequence> {
            return FnResult.success(value.toString())
        }

        override fun toString(): String {
            return "{create}"
        }
    }
}