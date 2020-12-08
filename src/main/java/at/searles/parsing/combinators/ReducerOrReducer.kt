package at.searles.parsing.combinators

import at.searles.parsing.ParserStream
import at.searles.parsing.Reducer
import at.searles.parsing.printing.PartialTree

open class ReducerOrReducer<T, U>(protected val choice0: Reducer<T, U>, protected val choice1: Reducer<T, U>) : Reducer<T,U> {

    override fun parse(left: T, stream: ParserStream): U? {
        return stream.reduce(left, choice0) ?: stream.reduce(left, choice1)
    }

    override fun recognize(stream: ParserStream): Boolean {
        return stream.recognize(choice0) || stream.recognize(choice1)
    }

    override fun print(item: U): PartialTree<T>? {
        return choice0.print(item) ?: choice1.print(item)
    }

    override fun toString(): String {
        return "$choice0.or($choice1)"
    }
}