package at.searles.parsing.parser.combinators

import at.searles.parsing.parser.ParserResult
import at.searles.parsing.parser.ParserStream
import at.searles.parsing.parser.Reducer
import at.searles.parsing.printer.PartialPrintResult
import at.searles.parsing.printer.PrintTree

class RepeatReducer<A>(private val reducer: Reducer<A, A>) : Reducer<A, A> {
    override fun parse(stream: ParserStream, input: A): ParserResult<A> {
        val startIndex = stream.index
        var endIndex = startIndex

        var value = input

        while(true) {
            val result = reducer.parse(stream, value)

            if(!result.isSuccess) {
                return ParserResult.success(value, startIndex, endIndex - startIndex)
            }

            endIndex = result.index + result.length
            value = result.value
        }
    }

    override fun print(value: A): PartialPrintResult<A> {
        var leftValue = value
        var output: PrintTree = PrintTree.Empty

        while(true) {
            val result = reducer.print(leftValue)

            if(!result.isSuccess) {
                return PartialPrintResult.success(leftValue, output)
            }

            output = result.output + output
            leftValue = result.value
        }
    }
}
