package at.searles.parsing.combinators

import at.searles.parsing.Recognizable.Then
import at.searles.parsing.Recognizer
import at.searles.parsing.printing.ConcreteSyntaxTree

/**
 *
 */
class RecognizerThenRecognizer(override val left: Recognizer, override val right: Recognizer) : Recognizer, Then {

    override fun print(): ConcreteSyntaxTree {
        return left.print().consRight(right.print())
    }

    override fun toString(): String {
        return createString()
    }

}