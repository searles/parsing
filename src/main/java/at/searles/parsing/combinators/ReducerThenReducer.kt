package at.searles.parsing.combinators

import at.searles.parsing.ParserStream
import at.searles.parsing.Recognizable.Then
import at.searles.parsing.Reducer
import at.searles.parsing.printing.PartialConcreteSyntaxTree

/**
 * Reducer followed by a reducer
 */
class ReducerThenReducer<T, U, V>(override val left: Reducer<T, U>, override val right: Reducer<U, V>) : Reducer<T, V>, Then {

    override fun parse(stream: ParserStream, input: T): V? {

        val offset = stream.offset
        val preStart = stream.start
        val preEnd = stream.end

        val leftOut = this.left.parse(stream, input)

        assert(stream.start == preStart)

        if (leftOut == null) {
            return null
        }

        val rightOut = right.parse(stream, leftOut)
        assert(stream.start == preStart)

        if (rightOut == null) {
            stream.requestBacktrackToOffset(this, offset)
            stream.end = preEnd
            return null
        }

        return rightOut
    }

    override fun recognize(stream: ParserStream): Boolean {
        val preStart = stream.start
        val status: Boolean = super.recognize(stream)

        if (status) {
            stream.start = preStart
        }

        return status
    }

    override fun print(item: V): PartialConcreteSyntaxTree<T>? {
        val midTree = right.print(item) ?: return null
        val leftTree = left.print(midTree.left) ?: return null
        return PartialConcreteSyntaxTree(leftTree.left, leftTree.right.consRight(midTree.right))
    }

    override fun toString(): String {
        return createString()
    }
}