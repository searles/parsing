package at.searles.parsing.combinators

import at.searles.parsing.Recognizable
import at.searles.parsing.Recognizer
import at.searles.parsing.printing.ConcreteSyntaxTree

class RecognizerOrRecognizer(override val choice0: Recognizer, override val choice1: Recognizer) : Recognizer, Recognizable.Or {
    override fun print(): ConcreteSyntaxTree {
        return choice0.print()
    }

    override fun toString(): String {
        return createString()
    }
}