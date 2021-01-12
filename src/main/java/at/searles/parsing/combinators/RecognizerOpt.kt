package at.searles.parsing.combinators

import at.searles.parsing.ParserStream
import at.searles.parsing.Recognizer
import at.searles.parsing.printing.ConcreteSyntaxTree
import at.searles.parsing.printing.EmptyTree

/**
 * Optional recognizer. This one is different from reducer and parser
 * because its printer is not semantically equivalent to A | epsilon.
 */
class RecognizerOpt(private val parent: Recognizer, private val alwaysPrint: Boolean = false) : Recognizer {
    override fun recognize(stream: ParserStream): Boolean {
        stream.recognize(parent, true)
        return true
    }

    override fun print(): ConcreteSyntaxTree {
        return if(alwaysPrint) parent.print() else EmptyTree
    }

    override fun toString(): String {
        return "$parent.opt"
    }
}