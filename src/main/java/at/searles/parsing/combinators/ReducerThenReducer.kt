package at.searles.parsing.combinators

import at.searles.parsing.ParserStream
import at.searles.parsing.Reducer
import at.searles.parsing.printing.PartialTree

/**
 * Reducer followed by a reducer
 */
class ReducerThenReducer<T, U, V>(private val left: Reducer<T, U>, private val right: Reducer<U, V>) : Reducer<T, V> {

    override fun parse(left: T, stream: ParserStream): V? {
        val m = stream.reduce(left, this.left) ?: return null
        return stream.reduce(m, right)
    }

    override fun recognize(stream: ParserStream): Boolean {
        return stream.recognize(left) && stream.recognize(right)
    }

    override fun print(item: V): PartialTree<T>? {
        val midTree = right.print(item) ?: return null
        val leftTree = left.print(midTree.left) ?: return null
        return PartialTree(leftTree.left, leftTree.right.consRight(midTree.right))
    }

    override fun toString(): String {
        return "$left.plus($right)"
    }
}