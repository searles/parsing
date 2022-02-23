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
            TokenRecognizer(lexer.createToken(Text.itext(it)), lexer, it)
        }.reduce { a, b -> a + b }
    }

    fun text(vararg text: String): Recognizer {
        return text.map<String, Recognizer> {
            TokenRecognizer(lexer.createToken(Text(it)), lexer, it)
        }.reduce { a, b -> a + b }
    }

    fun ch(char: Char): Recognizer {
        val token = lexer.createToken(CharSet(char))
        return TokenRecognizer(token, lexer, char.toString())
    }

    fun rex(regexp: Regexp): Parser<String> {
        return rex(regexp) { it.toString() }
    }

    fun rex(regexpString: String): Parser<String> {
        return rex(regexpString) { it.toString() }
    }

    fun <A> rex(regexpString: String, create: (CharSequence) -> A): Parser<A> {
        val regexp = RegexpGrammar.regexp.parse(regexpString).value
        return rex(regexp, create)
    }


    val eof get() = TokenRecognizer(lexer.createToken(CharSet.eof()), lexer, "")

    fun <A> rex(regexp: Regexp, create: (CharSequence) -> A): Parser<A> {
        return TokenParser(lexer.createToken(regexp), lexer, create)
    }
}