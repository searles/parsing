package at.searles.parsing.combinators

import at.searles.parsing.ParserStream
import at.searles.parsing.Reducer
import at.searles.parsing.printing.PartialTree

/**
 * Reducer followed by a reducer
 */
class ReducerThenReducer<T, U, V>(private val left: Reducer<T, U>, private val right: Reducer<U, V>) : Reducer<T, V> {

    override fun reduce(left: T, stream: ParserStream): V? {
        return stream.reduce(left, this.left)?.let {
            stream.reduce(it, this.right)
        }
    }

    override fun recognize(stream: ParserStream): Boolean {
        return stream.recognize(this.left, false) && stream.recognize(this.right, false)
    }

    override fun print(item: V): PartialTree<T>? {
        val midTree = right.print(item) ?: return null
        val leftTree = left.print(midTree.left) ?: return null
        return PartialTree(leftTree.left, leftTree.right.consRight(midTree.right))
    }

    override fun <W> plus(right: Reducer<V, W>): Reducer<T, W> {
        return ReducerThenReducer(left, this.right + right)
    }

    override fun toString(): String {
        return "$left.plus($right)"
    }
}