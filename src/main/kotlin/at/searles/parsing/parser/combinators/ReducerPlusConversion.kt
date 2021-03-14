package at.searles.parsing.parser.combinators

import at.searles.parsing.parser.Conversion
import at.searles.parsing.parser.ParserResult
import at.searles.parsing.parser.ParserStream
import at.searles.parsing.parser.Reducer
import at.searles.parsing.printer.PartialPrintTree

class ReducerPlusConversion<A, B, C>(private val reducer: Reducer<A, B>, private val conversion: Conversion<B, C>) : Reducer<A, C> {
    override fun parse(stream: ParserStream, input: A): ParserResult<C> {
        val result = reducer.parse(stream, input)

        if(!result.isSuccess) {
            return ParserResult.failure
        }

        return ParserResult.of(conversion.convert(result.value), result.index, result.length)
    }

    override fun print(value: C): PartialPrintTree<A> {
        val result = conversion.invert(value)

        if(!result.isSuccess) {
            return PartialPrintTree.failure
        }

        return reducer.print(result.value)
    }
}
