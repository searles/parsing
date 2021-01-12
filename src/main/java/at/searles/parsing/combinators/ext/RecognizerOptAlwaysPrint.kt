package at.searles.parsing.combinators.ext

import at.searles.parsing.ParserStream
import at.searles.parsing.Recognizer
import at.searles.parsing.combinators.RecognizerOpt
import at.searles.parsing.printing.ConcreteSyntaxTree
import at.searles.parsing.printing.EmptyTree

/**
 * Optional recognizer. This one is different from reducer and parser
 * because its printer is not semantically equivalent to A | epsilon.
 */
class RecognizerOptAlwaysPrint(private val parent: Recognizer) : Recognizer {
    override fun print(): ConcreteSyntaxTree {
        return parent.print()
    }

    override fun recognize(stream: ParserStream): Boolean {
        stream.recognize(parent, true)
        return true
    }

    override fun toString(): String {
        return "$parent.opt"
    }
}