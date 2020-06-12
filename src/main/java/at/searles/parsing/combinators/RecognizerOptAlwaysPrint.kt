package at.searles.parsing.combinators

import at.searles.parsing.Recognizable.Opt
import at.searles.parsing.Recognizer
import at.searles.parsing.printing.ConcreteSyntaxTree

/**
 * Optional recognizer. This one is different from reducer and parser
 * because its printer is not semantically equivalent to A | epsilon.
 */
class RecognizerOptAlwaysPrint(override val parent: Recognizer) : Recognizer, Opt {
    override fun print(): ConcreteSyntaxTree {
        return parent.print()
    }

    override fun toString(): String {
        return createString()
    }
}