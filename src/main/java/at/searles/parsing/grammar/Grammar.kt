package at.searles.parsing.grammar

import at.searles.buf.Frame
import at.searles.lexer.Tokenizer
import at.searles.parsing.Mapping
import at.searles.parsing.Parser
import at.searles.parsing.Recognizer
import at.searles.parsing.tokens.TokenParser
import at.searles.parsing.tokens.TokenRecognizer
import at.searles.regexp.CharSet
import at.searles.regexp.Regexp
import at.searles.regexp.Text
import kotlin.streams.toList

open class Grammar<T: Tokenizer>(val tokenizer: T) {
    fun eof(): TokenRecognizer {
        val regexp = CharSet.eof()
        val tokenId = tokenizer.add(regexp)
        return TokenRecognizer(tokenId, tokenizer, "")
    }

    fun <T> regexp(regexp: Regexp, conversion: (Frame) -> T): Parser<T> {
        val mapping = Mapping.create<CharSequence, T>( { it.toString() } ) { frame -> conversion(frame as Frame) }
        val tokenId = tokenizer.add(regexp)
        return TokenParser(tokenId, tokenizer) + mapping
    }

    private fun textInternal(text: String): TokenRecognizer {
        val regex = Text(text)
        val tokenId = tokenizer.add(regex)
        return TokenRecognizer(tokenId, tokenizer, text)
    }

    fun text(vararg texts: String): Recognizer {
        require(texts.isNotEmpty())
        return texts.map { textInternal(it) as Recognizer }.reduce { rec0, rec1 -> rec0 + rec1 }
    }

    private fun itextInternal(text: String): TokenRecognizer {
        require(text.isNotEmpty())

        val regexps = text.codePoints().mapToObj { convertToCaseInsensitiveRegexp(it) }.toList()

        val head = regexps.first()
        val tail = regexps.subList(1, regexps.size)

        val regexp = tail.fold(head) { prefix, codePoint -> prefix + codePoint }

        val tokenId = tokenizer.add(regexp)
        return TokenRecognizer(tokenId, tokenizer, text)
    }

    fun itext(vararg texts: String): Recognizer {
        require(texts.isNotEmpty())
        return texts.map { itextInternal(it) as Recognizer }.reduce { rec0, rec1 -> rec0 + rec1 }
    }

    private fun convertToCaseInsensitiveRegexp(codePoint: Int): Regexp {
        return when {
            codePoint in upperCaseRange -> CharSet.chars(codePoint, codePoint + caseAddend)
            codePoint in lowerCaseRange -> CharSet.chars(codePoint - caseAddend, codePoint)
            else -> CharSet.chars(codePoint)
        }
    }

    companion object {
        private val upperCaseRange = 'A'.toInt() .. 'Z'.toInt()
        private val lowerCaseRange = 'a'.toInt() .. 'z'.toInt()
        private const val caseAddend = 'a'.toInt() - 'A'.toInt()
    }
}
