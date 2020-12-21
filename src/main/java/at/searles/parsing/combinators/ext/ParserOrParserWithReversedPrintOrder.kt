package at.searles.parsing.combinators.ext

import at.searles.parsing.Parser
import at.searles.parsing.combinators.ParserOrParser
import at.searles.parsing.printing.ConcreteSyntaxTree

/**
 * Parser for options. The order is important. First one to
 * succeed is the one that is used.
 */
class ParserOrParserWithReversedPrintOrder<T>(vararg choices: Parser<T>) : ParserOrParser<T>(*choices) {

    private val reversedChoices by lazy { choices.reversed() }

    override fun print(item: T): ConcreteSyntaxTree? {
        for(choice in reversedChoices) {
            choice.print(item)?.let {
                return it
            }
        }

        return null
    }
}