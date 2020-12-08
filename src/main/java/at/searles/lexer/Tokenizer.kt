package at.searles.lexer

import at.searles.buf.Frame
import at.searles.lexer.utils.IntSet
import at.searles.regexp.Regexp

interface Tokenizer {
    val lexer: Lexer

    fun getCurrentTokenIds(stream: TokenStream): IntSet?

    /**
     * Returns whether this tokenizer accepts the current element
     * in the token stream. If so, the token stream continues
     * to the next element.
     * @return null if the tokenizer does not recognize this element.
     */
    fun matchToken(stream: TokenStream, tokenId: Int): Frame? {
        val currentTokenIds = getCurrentTokenIds(stream)
        if (currentTokenIds != null && currentTokenIds.contains(tokenId)) {
            stream.advance(tokenId)
            return stream.frame
        }
        return null
    }

    fun add(regexp: Regexp): Int
}