package at.searles.parsing.combinators.ext

import at.searles.parsing.ParserStream
import at.searles.parsing.Reducer
import at.searles.parsing.Reducer.Companion.rep
import at.searles.parsing.printing.PartialTree

class ReducerPlus<T>(private val reducer: Reducer<T, T>, private val minCount: Int) : Reducer<T, T> {

    private val parser: Reducer<T, T>
    private val printer: Reducer<T, T>

    init {
        require(minCount >= 1) { "minCount must be >= 1" }

        var sequence: Reducer<T, T> = reducer
        for (i in 1 until minCount) {
            sequence += reducer
        }

        parser = sequence + reducer.rep()
        printer = reducer.rep() + sequence
    }

    override fun reduce(left: T, stream: ParserStream): T? {
        return parser.reduce(left, stream)
    }

    override fun print(item: T): PartialTree<T>? {
        return printer.print(item)
    }

    override fun recognize(stream: ParserStream): Boolean {
        return parser.recognize(stream)
    }

    override fun toString(): String {
        return "$reducer.rep[$minCount]"
    }
}