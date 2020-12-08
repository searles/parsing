package at.searles.parsing.combinators

import at.searles.parsing.ParserStream
import at.searles.parsing.Recognizer
import at.searles.parsing.printing.ConcreteSyntaxTree

/**
 * Optional recognizer. This one is different from reducer and parser
 * because its printer is not semantically equivalent to A | epsilon.
 */
open class RecognizerOpt(protected val parent: Recognizer) : Recognizer {
    override fun recognize(stream: ParserStream): Boolean {
        stream.recognize(parent)
        return true
    }

    override fun print(): ConcreteSyntaxTree {
        return ConcreteSyntaxTree.empty()
    }

    override fun toString(): String {
        return "$parent.opt"
    }
}