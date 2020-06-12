package at.searles.parsing.combinators

import at.searles.parsing.ParserStream
import at.searles.parsing.Reducer
import at.searles.parsing.Reducer.Companion.rep
import at.searles.parsing.printing.PartialConcreteSyntaxTree

class ReducerPlus<T>(val reducer: Reducer<T, T>, val minCount: Int) : Reducer<T, T> {

    private val parser: Reducer<T, T>
    private val printer: Reducer<T, T>

    init {
        require(minCount >= 1) { "minCount must be >= 1" }
        var sequence: Reducer<T, T> = reducer
        for (i in 1 until minCount) {
            sequence = sequence.then(reducer)
        }

        parser = sequence.then(reducer.rep())
        printer = reducer.rep().then(sequence)
    }

    override fun parse(stream: ParserStream, input: T): T? {
        return parser.parse(stream, input)
    }

    override fun print(item: T): PartialConcreteSyntaxTree<T>? {
        return printer.print(item)
    }

    override fun recognize(stream: ParserStream): Boolean {
        return parser.recognize(stream)
    }

    override fun toString(): String {
        return reducer.toString() + if (minCount == 1) "+" else "{$minCount}"
    }
}