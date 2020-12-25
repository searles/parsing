package at.searles.parsing.combinators.ext

import at.searles.parsing.ParserStream
import at.searles.parsing.Recognizer
import at.searles.parsing.Reducer
import at.searles.parsing.Reducer.Companion.rep
import at.searles.parsing.printing.PartialTree

class ReducerJoinPlus<T>(private val reducer: Reducer<T, T>, private val separator: Recognizer) : Reducer<T, T> {

    private val parserReducer: Reducer<T, T> = reducer + (separator + reducer).rep()
    private val printerReducer: Reducer<T, T> = (reducer + separator).rep() + reducer

    override fun reduce(left: T, stream: ParserStream): T? {
        return parserReducer.reduce(left, stream)
    }

    override fun print(item: T): PartialTree<T>? {
        return printerReducer.print(item)
    }

    override fun recognize(stream: ParserStream): Boolean {
        return parserReducer.recognize(stream)
    }

    override fun toString(): String {
        return "$reducer.rep1($separator)"
    }

}