package at.searles.parsing.combinators

import at.searles.parsing.*
import at.searles.parsing.printing.ConcreteSyntaxTree

/**
 * Parser for chaining parsers. Eg for 5 + 6 where + 6 is the reducer.
 */
class ParserThenReducer<T, U>(private val left: Parser<T>, private val right: Reducer<T, U>) : Parser<U> {
    override fun parse(stream: ParserStream): U? {
        return stream.parse(left, true)?.let {
            stream.reduce(it, right)
        }
    }

    override fun recognize(stream: ParserStream): Boolean {
        return stream.recognize(left, true) && stream.recognize(right, false)
    }

    override fun print(item: U): ConcreteSyntaxTree? {
        val reducerOutput = right.print(item) ?: return null
        val parserOutput = left.print(reducerOutput.left) ?: return null
        return parserOutput.consRight(reducerOutput.right)
    }

    override fun <V> plus(right: Reducer<U, V>): Parser<V> {
        return ParserThenReducer(left, this.right + right)
    }

    override fun toString(): String {
        return "$left.then($right)"
    }
}