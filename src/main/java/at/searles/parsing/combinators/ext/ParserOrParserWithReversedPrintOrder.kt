package at.searles.parsing.combinators.ext

import at.searles.parsing.Parser
import at.searles.parsing.combinators.ParserOrParser
import at.searles.parsing.printing.ConcreteSyntaxTree

/**
 * Parser for options. The order is important. First one to
 * succeed is the one that is used.
 */
class ParserOrParserWithReversedPrintOrder<T>(choice0: Parser<T>, choice1: Parser<T>) : ParserOrParser<T>(choice0, choice1) {
    override fun print(item: T): ConcreteSyntaxTree? {
        return choice1.print(item) ?: choice0.print(item)
    }
}