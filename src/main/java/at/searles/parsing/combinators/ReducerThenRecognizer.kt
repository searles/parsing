package at.searles.parsing.combinators

import at.searles.parsing.ParserStream
import at.searles.parsing.Recognizable.Then
import at.searles.parsing.Recognizer
import at.searles.parsing.Reducer
import at.searles.parsing.printing.PartialConcreteSyntaxTree

class ReducerThenRecognizer<T, U>(override val left: Reducer<T, U>, override val right: Recognizer) : Reducer<T, U>, Then {
    override fun parse(stream: ParserStream, input: T): U? {
        val offset = stream.offset
        val preStart = stream.start
        val preEnd = stream.end
        val result = this.left.parse(stream, input)
        assert(stream.start == preStart)
        if (result == null) {
            return null
        }
        if (!right.recognize(stream)) {
            if (stream.offset != offset) {
                stream.requestBacktrackToOffset(this, offset)
            }
            assert(stream.start == preStart)
            stream.end = preEnd
            return null
        }
        stream.start = preStart
        return result
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
        val leftOutput = left.print(item) ?: return null
        val rightOutput = right.print()
        return PartialConcreteSyntaxTree(leftOutput.left, leftOutput.right.consRight(rightOutput))
    }

    override fun toString(): String {
        return createString()
    }
}