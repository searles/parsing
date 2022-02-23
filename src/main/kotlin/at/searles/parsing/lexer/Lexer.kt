package at.searles.parsing.lexer

import at.searles.parsing.codepoint.FrameStream
import at.searles.parsing.lexer.fsa.IntSet
import at.searles.parsing.lexer.fsa.Automaton
import at.searles.parsing.lexer.fsa.RegexpToFsaVisitor
import at.searles.parsing.lexer.regexp.Regexp

/**
 * Pretty much a more useable frontend for FSA. A lexer reads tokens from a buf.
 * It always finds the longest match. Each regex that is added returns a unique
 * integer. Multiple regexes can match an element (for instance, a keyword is
 * usually also an ID). In this case, both elements match.
 * The lexer does not take care of hidden tokens, this is the duty of
 * TokStream.
 */
class Lexer(private val tokenIdStream: Iterator<Int> = generateSequence(0) { it + 1 }.iterator()) {
    /**
     * fsa that accepts our current language. Empty language is not allowed,
     * add must be called at least once.
     */
    private var automaton = Automaton()

    /**
     * Fetches the next token from the token stream.
     * @param stream A frame stream in reset position, ie,
     * either reset or advance should have
     * been called prior to this call.
     * @return A set that should not be modified.
     */
    fun selectNextToken(stream: FrameStream): IntSet? {
        require(stream.isReset)
        val node = automaton.accept(stream) ?: run {
            stream.reset()
            return null
        }

        return node.acceptedIds
    }

    fun createToken(regexp: Regexp): Int {
        val regexpAutomaton = regexp.accept(RegexpToFsaVisitor)
        regexpAutomaton.setId(temporaryId)
        automaton = automaton.union(regexpAutomaton)

        return replaceTemporaryIdByExistingOrNewId()
    }

    private fun replaceTemporaryIdByExistingOrNewId(): Int {
        val idsOfNonMatches = getIdsOfNodesWithoutTemporaryId()

        val idsOfMatches = getIdsOfNodesWithTemporaryId().apply {
            // remove token ids of intersecting tokens.
            removeAll(idsOfNonMatches)
        }

        require(idsOfMatches.contains(temporaryId))
        idsOfMatches.remove(temporaryId)
        require(idsOfMatches.size <= 1)

        return if(idsOfMatches.isEmpty) {
            replaceMarkerByNewTokenId()
        } else {
            removeMarker()
            idsOfMatches[0]
        }
    }

    private fun getIdsOfNodesWithoutTemporaryId(): IntSet {
        val set = IntSet()

        automaton.finalNodes.filter { !it.acceptedIds.contains(temporaryId) }.forEach {
            set.addAll(it.acceptedIds)
        }

        return set
    }

    private fun getIdsOfNodesWithTemporaryId(): IntSet {
        val set = IntSet()

        automaton.finalNodes.filter { it.acceptedIds.contains(temporaryId) }.forEach {
            if (set.isEmpty) {
                set.addAll(it.acceptedIds)
            } else {
                set.retainAll(it.acceptedIds)
            }
        }

        return set
    }

    private fun removeMarker() {
        automaton.finalNodes.forEach {
            it.acceptedIds.remove(-1)
        }
    }

    private fun replaceMarkerByNewTokenId(): Int {
        val tokenId = tokenIdStream.next()

        automaton.finalNodes.forEach {
            if (it.acceptedIds.contains(-1)) {
                it.acceptedIds.remove(-1)
                it.acceptedIds.add(tokenId)
            }
        }

        return tokenId
    }

    companion object {
        private const val temporaryId = -1
    }
}