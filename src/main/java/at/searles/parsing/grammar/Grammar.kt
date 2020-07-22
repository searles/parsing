package at.searles.parsing.grammar

import at.searles.buf.Frame
import at.searles.lexer.Tokenizer
import at.searles.parsing.Mapping
import at.searles.parsing.Parser
import at.searles.parsing.tokens.TokenParser
import at.searles.parsing.tokens.TokenRecognizer
import at.searles.regexp.CharSet
import at.searles.regexp.Regexp
import at.searles.regexp.Text

interface Grammar<T: Tokenizer> {

    val tokenizer: T

    fun eof(): TokenRecognizer {
        val regexp = CharSet.eof()
        val tokenId = tokenizer.add(regexp)
        return TokenRecognizer(tokenId, tokenizer, "")
    }

    fun <T> regexp(regexp: Regexp, conversion: (Frame) -> T): Parser<T> {
        val mapping = Mapping.create<CharSequence, T>( { it.toString() } ) { it -> conversion(it as Frame) }
        val tokenId = tokenizer.add(regexp)
        return TokenParser(tokenId, tokenizer) + mapping
    }

    fun text(text: String): TokenRecognizer {
        val regex = Text(text)
        val tokenId = tokenizer.add(regex)
        return TokenRecognizer(tokenId, tokenizer, text)
    }
}
