package at.searles.parsing.combinators

import at.searles.parsing.Fold
import at.searles.parsing.Parser
import at.searles.parsing.ParserStream
import at.searles.parsing.Reducer
import at.searles.parsing.printing.PartialConcreteSyntaxTree

/**
 * Creates a reducer out of a parser.
 */
class ParserToReducer<T, U, V>(private val parent: Parser<U>, private val fold: Fold<T, U, V>) : Reducer<T, V> {
    override fun parse(stream: ParserStream, input: T): V? {
        // must preserve start position.
        val leftStart = stream.start
        val right = parent.parse(stream) ?: return null
        stream.start = leftStart
        return fold.apply(stream, input, right)
    }

    override fun print(item: V): PartialConcreteSyntaxTree<T>? {
        val right = fold.rightInverse(item) ?: return null
        val left = fold.leftInverse(item) ?: return null
        val rightOutput = parent.print(right) ?: return null
        return PartialConcreteSyntaxTree(left, rightOutput)
    }

    override fun recognize(stream: ParserStream): Boolean {
        return parent.recognize(stream)
    }

    override fun toString(): String {
        return "$parent >> $fold"
    }

}