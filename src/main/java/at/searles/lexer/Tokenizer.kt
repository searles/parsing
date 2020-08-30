package at.searles.lexer

import at.searles.buf.Frame
import at.searles.lexer.utils.IntSet
import at.searles.regexp.Regexp

interface Tokenizer {
    val lexer: Lexer

    fun currentTokenIds(stream: TokenStream): IntSet?

    /**
     * Returns whether this tokenizer accepts the current element
     * in the token stream. If so, the token stream continues
     * to the next element.
     * @return null if the tokenizer does not recognize this element.
     */
    fun matchToken(stream: TokenStream, tokId: Int): Frame? {
        val currentTokenIds = currentTokenIds(stream)
        if (currentTokenIds != null && currentTokenIds.contains(tokId)) {
            stream.advance(tokId)
            return stream.frame
        }
        return null
    }

    fun add(regexp: Regexp): Int
}