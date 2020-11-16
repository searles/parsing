package at.searles.parsing.combinators

import at.searles.parsing.Parser
import at.searles.parsing.ParserStream
import at.searles.parsing.Recognizable.Then
import at.searles.parsing.Reducer
import at.searles.parsing.printing.ConcreteSyntaxTree

/**
 * Parser for chaining parsers. Eg for 5 + 6 where + 6 is the reducer.
 */
class ParserThenReducer<T, U>(override val left: Parser<T>, override val right: Reducer<T, U>) : Parser<U>, Then {

    override fun parse(stream: ParserStream): U? {
        val offset = stream.offset

        // to restore if backtracking
        val preStart = stream.start
        val preEnd = stream.end

        val t = left.parse(stream) ?: return null

        // reducer preserves start() in stream and only sets end().
        val u = right.parse(stream, t)

        if (u == null) {
            if (offset != stream.offset) {
                stream.requestBacktrackToOffset(this, offset)
            }

            stream.start = preStart
            stream.end = preEnd

            return null
        }

        return u
    }

    override fun print(item: U): ConcreteSyntaxTree? {
        val reducerOutput = right.print(item) ?: return null
        val parserOutput = left.print(reducerOutput.left) ?: return null
        return parserOutput.consRight(reducerOutput.right)
    }

    override fun toString(): String {
        return createString()
    }
}