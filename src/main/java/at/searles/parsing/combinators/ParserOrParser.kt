package at.searles.parsing.combinators

import at.searles.parsing.Parser
import at.searles.parsing.ParserStream
import at.searles.parsing.printing.ConcreteSyntaxTree

/**
 * Parser for options. The order is important. First one to
 * succeed is the one that is used.
 */
open class ParserOrParser<T>(open val choice0: Parser<T>, open val choice1: Parser<T>) : Parser<T> {
    override fun parse(stream: ParserStream): T? {
        return stream.parse(choice0) ?: stream.parse(choice1)
    }

    override fun recognize(stream: ParserStream): Boolean {
        return stream.recognize(choice0) || stream.recognize(choice1)
    }

    override fun print(item: T): ConcreteSyntaxTree? {
        return choice0.print(item) ?: choice1.print(item)
    }

    override fun toString(): String {
        return "$choice0.or($choice1)"
    }

}