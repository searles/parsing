package at.searles.parsing.parser.combinators

import at.searles.parsing.parser.ParserResult
import at.searles.parsing.parser.ParserStream
import at.searles.parsing.parser.Reducer
import at.searles.parsing.printer.PartialPrintTree

class ReducerUnion<A, B>(private val reducers: List<Reducer<A, B>>) : Reducer<A, B> {
    override fun parse(stream: ParserStream, input: A): ParserResult<B> {
        for(reducer in reducers) {
            val result = reducer.parse(stream, input)

            if(result.isSuccess) {
                return result
            }
        }

        return ParserResult.failure
    }

    override fun print(value: B): PartialPrintTree<A> {
        for(reducer in reducers) {
            val result = reducer.print(value)

            if(result.isSuccess) {
                return result
            }
        }

        return PartialPrintTree.failure
    }

    override fun or(other: Reducer<A, B>): Reducer<A, B> {
        return ReducerUnion(reducers + other)
    }

    override fun toString(): String {
        return "union(${reducers.joinToString(", ")})"
    }
}
