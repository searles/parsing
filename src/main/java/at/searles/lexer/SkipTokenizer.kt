package at.searles.lexer

import at.searles.lexer.utils.IntSet
import at.searles.regexp.Regexp

class SkipTokenizer(override val lexer: Lexer) : Tokenizer {

    private val skippedTokenIds: IntSet = IntSet()

    fun addSkipped(regexp: Regexp): Int {
        val tokenId = add(regexp)
        skippedTokenIds.add(tokenId)
        return tokenId
    }

    override fun add(regexp: Regexp): Int {
        return lexer.add(regexp)
    }

    override fun getCurrentTokenIds(stream: TokenStream): IntSet? {
        var currentTokenIds = lexer.getCurrentTokenIds(stream)

        while (currentTokenIds != null) {
            val index = currentTokenIds.indexOfFirstMatch(skippedTokenIds)

            if (index != -1) {
                stream.advance(currentTokenIds[index])
                currentTokenIds = lexer.getCurrentTokenIds(stream)
            } else {
                break
            }
        }

        return currentTokenIds
    }
}