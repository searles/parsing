package at.searles.parsing.combinators

import at.searles.parsing.Parser
import at.searles.parsing.printing.ConcreteSyntaxTree

/**
 * Parser for options. The order is important. First one to
 * succeed is the one that is used.
 */
class ParserOrParserWithReversedPrintOrder<T>(override val choice0: Parser<T>, override val choice1: Parser<T>) : ParserOrParser<T>(choice0, choice1) {

    override fun print(item: T): ConcreteSyntaxTree? {
        return choice1.print(item) ?: choice0.print(item)
    }
}