package at.searles.parsing.combinators

import at.searles.parsing.ParserStream
import at.searles.parsing.Recognizable
import at.searles.parsing.Reducer
import at.searles.parsing.printing.PartialConcreteSyntaxTree

open class ReducerOrReducer<T, U>(override val choice0: Reducer<T, U>, override val choice1: Reducer<T, U>) : Reducer<T, U>, Recognizable.Or {

    override fun parse(stream: ParserStream, input: T): U? {
        return choice0.parse(stream, input) ?: choice1.parse(stream, input)
    }

    override fun print(item: U): PartialConcreteSyntaxTree<T>? {
        return choice0.print(item) ?: choice1.print(item)
    }

    override fun toString(): String {
        return createString()
    }
}