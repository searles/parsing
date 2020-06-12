package at.searles.parsing.combinators

import at.searles.parsing.ParserStream
import at.searles.parsing.Recognizable.Then
import at.searles.parsing.Recognizer
import at.searles.parsing.Reducer
import at.searles.parsing.printing.PartialConcreteSyntaxTree

class RecognizerThenReducer<T, U>(override val left: Recognizer, override val right: Reducer<T, U>) : Reducer<T, U>, Then {

    override fun parse(stream: ParserStream, input: T): U? {
        val offset = stream.offset
        val preStart = stream.start
        val preEnd = stream.end

        if (!left.recognize(stream)) {
            return null
        }

        stream.start = preStart

        val u = right.parse(stream, input)

        if (u == null) {
            if (stream.offset != offset) {
                stream.requestBacktrackToOffset(this, offset)
            }

            stream.end = preEnd
            return null
        }

        assert(preStart == stream.start)

        return u
    }

    override fun recognize(stream: ParserStream): Boolean {
        val preStart = stream.start
        val status: Boolean = super.recognize(stream)

        if (status) {
            stream.start = preStart
        }

        return status
    }

    override fun print(item: U): PartialConcreteSyntaxTree<T>? {
        val output = right.print(item) ?: return null
        val leftOutput = left.print()
        return PartialConcreteSyntaxTree(output.left, output.right.consLeft(leftOutput))
    }

    override fun toString(): String {
        return createString()
    }

}