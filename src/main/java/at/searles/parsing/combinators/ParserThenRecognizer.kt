package at.searles.parsing.combinators

import at.searles.parsing.Parser
import at.searles.parsing.ParserStream
import at.searles.parsing.Recognizable.Then
import at.searles.parsing.Recognizer
import at.searles.parsing.printing.ConcreteSyntaxTree

class ParserThenRecognizer<T>(override val left: Parser<T>, override val right: Recognizer) : Parser<T>, Then {
    override fun parse(stream: ParserStream): T? {
        val offset = stream.offset

        // to restore if backtracking
        val preStart = stream.start
        val preEnd = stream.end
        val result = left.parse(stream) ?: return null

        // The start position of left.
        val start = stream.start

        if (!right.recognize(stream)) {
            if (stream.offset != offset) {
                stream.requestBacktrackToOffset(this, offset)
            }

            stream.start = preStart
            stream.end = preEnd
            return null
        }

        stream.start = start
        return result
    }

    override fun print(item: T): ConcreteSyntaxTree? {
        val output = left.print(item)

        // Recognizer.printTo always succeeds.
        return output?.consRight(right.print())
    }

    override fun toString(): String {
        return createString()
    }
}