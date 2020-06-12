package at.searles.parsing.combinators

import at.searles.parsing.Parser
import at.searles.parsing.ParserStream
import at.searles.parsing.Recognizable
import at.searles.parsing.printing.ConcreteSyntaxTree

/**
 * Parser for options. The order is important. First one to
 * succeed is the one that is used.
 */
open class ParserOrParser<T>(override val choice0: Parser<T>, override val choice1: Parser<T>) : Parser<T>, Recognizable.Or {
    override fun parse(stream: ParserStream): T? {
        return choice0.parse(stream) ?: choice1.parse(stream)
    }

    override fun print(item: T): ConcreteSyntaxTree? {
        return choice0.print(item) ?: choice1.print(item)
    }

    override fun toString(): String {
        return createString()
    }
}