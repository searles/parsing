package at.searles.parsing.combinators

import at.searles.parsing.ParserStream
import at.searles.parsing.Recognizer
import at.searles.parsing.printing.ConcreteSyntaxTree
import at.searles.parsing.printing.EmptyTree

/**
 * Possibly empty repetition
 */
class RecognizerRep(private val parent: Recognizer) : Recognizer {
    override fun recognize(stream: ParserStream): Boolean {
        if(stream.recognize(parent, true)) {
            while(stream.recognize(parent, false)) {
                // empty
            }
        }

        return true
    }

    override fun print(): ConcreteSyntaxTree {
        return EmptyTree
    }

    override fun toString(): String {
        return "$parent.rep"
    }
}