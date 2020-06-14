package at.searles.parsing.combinators

import at.searles.parsing.ParserStream
import at.searles.parsing.Recognizer
import at.searles.parsing.Reducer
import at.searles.parsing.Reducer.Companion.rep
import at.searles.parsing.printing.PartialConcreteSyntaxTree

class ReducerJoinPlus<T>(separator: Recognizer, reducer: Reducer<T, T>) : Reducer<T, T> {

    private val parserReducer: Reducer<T, T> = reducer + (separator + reducer).rep()
    private val printerReducer: Reducer<T, T> = (reducer + separator).rep() + reducer

    override fun parse(stream: ParserStream, input: T): T? {
        return parserReducer.parse(stream, input)
    }

    override fun print(item: T): PartialConcreteSyntaxTree<T>? {
        return printerReducer.print(item)
    }

    override fun recognize(stream: ParserStream): Boolean {
        return parserReducer.recognize(stream)
    }

    override fun toString(): String {
        return String.format("join(%s)", parserReducer.toString())
    }

}