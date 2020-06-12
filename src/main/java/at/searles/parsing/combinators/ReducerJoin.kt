package at.searles.parsing.combinators

import at.searles.parsing.ParserStream
import at.searles.parsing.Recognizer
import at.searles.parsing.Reducer
import at.searles.parsing.Reducer.Companion.opt
import at.searles.parsing.Reducer.Companion.rep
import at.searles.parsing.printing.PartialConcreteSyntaxTree

class ReducerJoin<T>(separator: Recognizer, reducer: Reducer<T, T>) : Reducer<T, T> {
    private val parserReducer: Reducer<T, T> = reducer.then(separator.then(reducer).rep()).opt()
    private val printerReducer: Reducer<T, T> = reducer.then(separator).rep().then(reducer).opt()

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