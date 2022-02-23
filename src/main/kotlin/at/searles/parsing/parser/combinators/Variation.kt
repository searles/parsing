package at.searles.parsing.parser.combinators

import at.searles.parsing.lexer.TokenStream
import at.searles.parsing.parser.ParserResult
import at.searles.parsing.parser.Reducer
import at.searles.parsing.printer.PartialPrintTree
import at.searles.parsing.printer.PrintTree

class Variation<A>(private val reducers: List<Reducer<A, A>>): Reducer<A, A> {
    override fun parse(stream: TokenStream, input: A): ParserResult<A> {
        val startIndex = stream.startIndex
        val list = ArrayList(reducers)

        var value = input
        var iterator = list.iterator()

        while(iterator.hasNext()) {
            val result = iterator.next().parse(stream, value)

            if(result.isSuccess) {
                iterator.remove() // Remove this to allow repetitions
                iterator = list.iterator()
                value = result.value
            }
        }

        // If no repetitions are allowed and the list is empty, then
        // it is a permutation.

        return ParserResult.of(value, startIndex, stream.startIndex - startIndex)
    }

    override fun print(value: A): PartialPrintTree<A> {
        val list = ArrayList(reducers)

        var output = value
        var iterator = list.iterator()

        var outputTree = PrintTree.empty

        while(iterator.hasNext()) {
            val result = iterator.next().print(output)

            if(result.isSuccess) {
                iterator.remove() // Remove this to allow repetitions
                iterator = list.iterator()
                output = result.leftValue
                outputTree = result.rightTree + outputTree
            }
        }

        // If no repetitions are allowed and the list is empty, then
        // it is a permutation.

        return PartialPrintTree.of(output, outputTree)
    }

    override fun toString(): String {
        return "variation(${reducers.joinToString(", ")})"
    }
}