package at.searles.parsing.format

import at.searles.parsing.ParserStream
import at.searles.parsing.Recognizer
import at.searles.parsing.printing.ConcreteSyntaxTree
import at.searles.parsing.printing.FormatTree

/**
 * Using this parser we obtain recursion.
 */
class Mark(private val marker: Any) : Recognizer {

    override fun recognize(stream: ParserStream): Boolean {
        stream.notifyMark(marker) // TODO I don't like this.
        return true
    }

    override fun print(): ConcreteSyntaxTree {
        return FormatTree(marker)
    }

    override fun toString(): String {
        return "[format:$marker]"
    }

}