package at.searles.parsing.ref

import at.searles.parsing.ParserStream
import at.searles.parsing.Recognizer
import at.searles.parsing.printing.ConcreteSyntaxTree

/**
 * Using this parser we obtain recursion.
 */
class RefRecognizer(private val label: String) : Recognizer {

    var ref: Recognizer
        get() = internalRef ?: error("$label is not initialized")

        set(value) {
            internalRef = value
        }

    private var internalRef: Recognizer? = null

    override fun recognize(stream: ParserStream): Boolean {
        return ref.recognize(stream)
    }

    override fun print(): ConcreteSyntaxTree {
        return ref.print()
    }

    override fun toString(): String {
        return label
    }
}