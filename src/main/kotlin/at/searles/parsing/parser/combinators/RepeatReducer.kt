package at.searles.parsing.parser.combinators

import at.searles.parsing.parser.ParserResult
import at.searles.parsing.parser.ParserStream
import at.searles.parsing.parser.Reducer
import at.searles.parsing.printer.PartialPrintTree
import at.searles.parsing.printer.PrintTree

class RepeatReducer<A>(private val reducer: Reducer<A, A>, private val minCount: Int) : Reducer<A, A> {
    init {
        require(minCount >= 0)
    }

    override fun parse(stream: ParserStream, input: A): ParserResult<A> {
        val state = stream.createState()

        val index0 = stream.index

        var value = input
        var count = 0

        while(true) {
            val result = reducer.parse(stream, value)

            if(!result.isSuccess) {
                if(count < minCount) {
                    stream.restoreState(state)
                    return ParserResult.failure
                }

                return ParserResult.of(value, index0, stream.index - index0)
            }

            value = result.value
            count++
        }
    }

    override fun print(value: A): PartialPrintTree<A> {
        var leftValue = value
        var output: PrintTree = PrintTree.Empty

        var count = 0

        while(true) {
            val result = reducer.print(leftValue)

            if(!result.isSuccess) {
                if(count < minCount) return PartialPrintTree.failure

                return PartialPrintTree.of(leftValue, output)
            }

            output = result.rightTree + output
            leftValue = result.leftValue
            count++
        }
    }

    override fun toString(): String {
        return "$reducer.rep($minCount)"
    }
}
