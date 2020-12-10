package at.searles.parsing.combinators

import at.searles.parsing.ParserStream
import at.searles.parsing.Recognizer
import at.searles.parsing.printing.ConcreteSyntaxTree
import at.searles.parsing.printing.EmptyTree

/**
 * Optional recognizer. This one is different from reducer and parser
 * because its printer is not semantically equivalent to A | epsilon.
 */
open class RecognizerOpt(protected val parent: Recognizer) : Recognizer {
    override fun recognize(stream: ParserStream): Boolean {
        stream.recognize(parent, true)
        return true
    }

    override fun print(): ConcreteSyntaxTree {
        return EmptyTree
    }

    override fun toString(): String {
        return "$parent.opt"
    }
}