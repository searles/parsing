package at.searles.parsing.format

import at.searles.parsing.ParserStream
import at.searles.parsing.Recognizer
import at.searles.parsing.printing.ConcreteSyntaxTree
import at.searles.parsing.printing.MarkedTree

/**
 * Using this parser we obtain recursion.
 */
class Mark(private val marker: Any) : Recognizer {

    override fun recognize(stream: ParserStream): Boolean {
        stream.notifyMark(marker)
        return true
    }

    override fun print(): ConcreteSyntaxTree {
        return MarkedTree(marker)
    }

    override fun toString(): String {
        return "[format:$marker]"
    }

}