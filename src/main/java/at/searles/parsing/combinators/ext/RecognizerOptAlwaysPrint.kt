package at.searles.parsing.combinators.ext

import at.searles.parsing.Recognizer
import at.searles.parsing.combinators.RecognizerOpt
import at.searles.parsing.printing.ConcreteSyntaxTree

/**
 * Optional recognizer. This one is different from reducer and parser
 * because its printer is not semantically equivalent to A | epsilon.
 */
class RecognizerOptAlwaysPrint(parent: Recognizer) : RecognizerOpt(parent) {
    override fun print(): ConcreteSyntaxTree {
        return parent.print()
    }
}