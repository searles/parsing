package at.searles.lexer

import at.searles.lexer.utils.IntSet
import at.searles.regexp.Regexp

class SkipTokenizer(private val parent: Tokenizer) : Tokenizer {

    private val skippedTokenIds: IntSet = IntSet()

    override val lexer get() = parent.lexer

    fun addSkipped(regexp: Regexp): Int {
        val tokenId = add(regexp)
        skippedTokenIds.add(tokenId)
        return tokenId
    }

    override fun currentTokenIds(stream: TokenStream): IntSet? {
        var currentTokenIds = parent.currentTokenIds(stream)
        while (currentTokenIds != null) {
            val index = currentTokenIds.indexOfFirstMatch(skippedTokenIds)
            if (index == -1) {
                // not a hidden symbol.
                break
            }
            stream.advance(currentTokenIds[index])
            currentTokenIds = parent.currentTokenIds(stream)
        }
        return currentTokenIds
    }

    override fun add(regexp: Regexp): Int {
        return parent.add(regexp)
    }
}