package at.searles.parsing.label

import at.searles.parsing.ParserStream
import at.searles.parsing.Recognizer
import at.searles.parsing.printing.ConcreteSyntaxTree
import at.searles.parsing.printing.FormatTree

/**
 * Using this parser we obtain recursion.
 */
class FormatRecognizer(private val marker: Any) : Recognizer {

    override fun recognize(stream: ParserStream): Boolean {
        stream.notifyFormat(marker)
        return true
    }

    override fun print(): ConcreteSyntaxTree {
        return FormatTree(marker)
    }

    override fun toString(): String {
        return "[${marker.toString()}]"
    }

}