package at.searles.parsing.combinators

import at.searles.parsing.Parser
import at.searles.parsing.ParserStream
import at.searles.parsing.Reducer
import at.searles.parsing.printing.ConcreteSyntaxTree

/**
 * Parser for options. The order is important. First one to
 * succeed is the one that is used.
 */
open class ParserOrParser<T>(private vararg val choices: Parser<T>) : Parser<T> {
    override fun or(other: Parser<T>): Parser<T> {
        return ParserOrParser(*choices, other)
    }

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
        for(choice in choices) {
            choice.print(item)?.let {
                return it
            }
        }

        return null
    }

    override fun toString(): String {
        return "${choices.first()}.or(${choices.drop(1).joinToString(", ")})"
    }

}