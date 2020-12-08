package at.searles.parsing.combinators

import at.searles.parsing.Parser
import at.searles.parsing.ParserStream
import at.searles.parsing.Recognizer
import at.searles.parsing.printing.ConcreteSyntaxTree

class RecognizerThenParser<T>(private val left: Recognizer, private val right: Parser<T>) : Parser<T> {

    override fun parse(stream: ParserStream): T? {
        if (!stream.recognize(left, true)) {
            return null
        }

        return stream.parse(right, false)
    }

    override fun recognize(stream: ParserStream): Boolean {
        return stream.recognize(left, true) && stream.recognize(right, false)
    }

    override fun print(item: T): ConcreteSyntaxTree? {
        val output = right.print(item)

        // printTo in recognizer always succeeds.
        return output?.consLeft(left.print())
    }

    override fun toString(): String {
        return "$left.then($right)"
    }

}