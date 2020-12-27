package at.searles.parsing.combinators

import at.searles.parsing.ParserStream
import at.searles.parsing.Recognizer
import at.searles.parsing.Reducer
import at.searles.parsing.printing.EmptyTree
import at.searles.parsing.printing.PartialTree

class ReducerOpt<T>(private val parent: Reducer<T, T>) : Reducer<T, T> {

    override fun reduce(left: T, stream: ParserStream): T? {
        return stream.reduce(left, parent) ?: left
    }

    override fun recognize(stream: ParserStream): Boolean {
        stream.recognize(parent, false)
        return true
    }

    override fun print(item: T): PartialTree<T> {
        val output = parent.print(item)
        return output ?: PartialTree(item, EmptyTree)
    }

    override fun <V> plus(right: Reducer<T, V>): Reducer<T, V> {
        return ReducerOrReducer(parent + right, right)
    }

    override fun plus(right: Recognizer): Reducer<T, T> {
        return ReducerOrReducer(parent + right, right.toReducer())
    }

    override fun toString(): String {
        return "$parent.opt"
    }
}