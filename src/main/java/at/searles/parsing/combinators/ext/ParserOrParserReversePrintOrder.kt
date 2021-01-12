package at.searles.parsing.combinators.ext

import at.searles.parsing.Parser
import at.searles.parsing.ParserStream
import at.searles.parsing.combinators.ParserOrParser
import at.searles.parsing.printing.ConcreteSyntaxTree

/**
 * Parser for options. The order is important. First one to
 * succeed is the one that is used.
 */
class ParserOrParserReversePrintOrder<T>(private vararg val choices: Parser<T>): Parser<T> {

    private val reversedChoices by lazy { choices.reversed() }

    override fun parse(stream: ParserStream): T? {
        for(choice in choices) {
            stream.parse(choice)?.let {
                return it
            }
        }

        return null
    }

    override fun recognize(stream: ParserStream): Boolean {
        for(choice in choices) {
            if(stream.recognize(choice, true)) {
                return true
            }
        }

        return false
    }

    override fun print(item: T): ConcreteSyntaxTree? {
        for(choice in reversedChoices) {
            choice.print(item)?.let {
                return it
            }
        }

        return null
    }
}