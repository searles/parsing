package at.searles.parsing.parser.tools

import at.searles.parsing.parser.Parser
import at.searles.parsing.parser.ParserResult
import at.searles.parsing.parser.ParserStream
import at.searles.parsing.printer.SelectRangePrintTree
import at.searles.parsing.printer.PrintTree

class SelectParser<A>(private val label: Any, private val parser: Parser<A>) : Parser<A> {
    override fun parse(stream: ParserStream): ParserResult<A> {
        val startState = stream.createState()
        val result = parser.parse(stream)

        if(result.isSuccess) {
            stream.notifySelection(label, startState)
        }

        return result
    }

    override fun print(value: A): PrintTree {
        val result = parser.print(value)

        if(!result.isSuccess) {
            return PrintTree.failure
        }

        return SelectRangePrintTree(label, result)
    }
}
