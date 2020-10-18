package at.searles.lexer

import at.searles.buf.FrameStream
import at.searles.lexer.utils.Counter
import at.searles.lexer.utils.IntSet
import at.searles.regexp.Regexp
import at.searles.regexp.Text
import at.searles.regexp.fsa.Automaton
import at.searles.regexp.fsa.RegexpToFsaVisitor

/**
 * Pretty much a more useable frontend for FSA. A lexer reads tokens from a buf.
 * It always finds the longest match. Each regex that is added returns a unique
 * integer. Multiple regexes can match an element (for instance, a keyword is
 * usually also an ID). In this case, both elements match.
 * The lexer does not take care of hidden tokens, this is the duty of
 * TokStream.
 */
class Lexer(val tokenIdProvider: Counter = Counter()) : Tokenizer {
    /**
     * fsa that accepts our current language. Empty language is not allowed,
     * add must be called at least once.
     */
    private var automaton = Automaton()

    override val lexer: Lexer get() = this

    override fun currentTokenIds(stream: TokenStream): IntSet? {
        return stream.getAcceptedTokens(this)
    }

    override fun add(regexp: Regexp): Int {
        val regexpAutomaton = regexp.accept(RegexpToFsaVisitor)

        // add -1 to regexpAutomaton
        regexpAutomaton.setId(marker)

        automaton = automaton.union(regexpAutomaton)

        val idsOfMatches = IntSet()
        val idsOfNonMatches = IntSet()

        automaton.finalNodes.forEach {
            if(it.set.contains(marker)) {
                if(idsOfMatches.isEmpty) {
                    idsOfMatches.addAll(it.set)
                } else {
                    idsOfMatches.retainAll(it.set)
                }
            } else {
                idsOfNonMatches.addAll(it.set)
            }
        }

        idsOfMatches.removeAll(idsOfNonMatches)
        require(idsOfMatches.contains(marker))

        idsOfMatches.remove(marker)

        require(idsOfMatches.size <= 1)

        return if(idsOfMatches.isEmpty) {
            replaceMarkerByNewId()
        } else {
            removeMarker()
            idsOfMatches[0]
        }
    }

    private fun removeMarker() {
        automaton.finalNodes.forEach {
            it.set.remove(-1)
        }
    }

    private fun replaceMarkerByNewId(): Int {
        val newId = tokenIdProvider.next()

        automaton.finalNodes.forEach {
            if (it.set.contains(-1)) {
                it.set.remove(-1)
                it.set.add(newId)
            }
        }

        return newId
    }

    /**
     * Creates a new token from a text
     */
    fun add(s: String): Int {
        return add(Text(s))
    }

    /**
     * Fetches the next token from the token stream.
     * @param stream A frame stream in reset position, ie,
     * either reset or advance should have
     * been called prior to this call.
     * @return A set that should not be modified.
     */
    fun readNextToken(stream: FrameStream): IntSet? {
        val node = automaton.accept(stream) ?: return null
        return node.set
    }

    companion object {
        private const val marker = -1
    }
}