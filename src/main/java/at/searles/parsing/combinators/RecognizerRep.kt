package at.searles.parsing.combinators

import at.searles.parsing.Recognizable.Rep
import at.searles.parsing.Recognizer
import at.searles.parsing.printing.ConcreteSyntaxTree

/**
 * Possibly empty repetition
 */
class RecognizerRep(override val parent: Recognizer) : Recognizer, Rep {
    override fun print(): ConcreteSyntaxTree {
        return ConcreteSyntaxTree.empty()
    }

    override fun toString(): String {
        return createString()
    }

}