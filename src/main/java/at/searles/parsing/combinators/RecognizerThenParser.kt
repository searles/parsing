package at.searles.parsing.combinators

import at.searles.parsing.Parser
import at.searles.parsing.ParserStream
import at.searles.parsing.Recognizable.Then
import at.searles.parsing.Recognizer
import at.searles.parsing.printing.ConcreteSyntaxTree

class RecognizerThenParser<T>(override val left: Recognizer, override val right: Parser<T>) : Parser<T>, Then {

    override fun parse(stream: ParserStream): T? {
        val offset = stream.offset
        val preStart = stream.start
        val preEnd = stream.end

        if (!left.recognize(stream)) {
            return null
        }

        val start = stream.start
        val rightResult = right.parse(stream)

        if (rightResult == null) {
            if (stream.offset != offset) {
                stream.requestBacktrackToOffset(this, offset)
            }
            stream.start = preStart
            stream.end = preEnd
            return null
        }

        stream.start = start

        return rightResult
    }

    override fun print(item: T): ConcreteSyntaxTree? {
        val output = right.print(item)

        // printTo in recognizer always succeeds.
        return output?.consLeft(left.print())
    }

    override fun toString(): String {
        return createString()
    }
}