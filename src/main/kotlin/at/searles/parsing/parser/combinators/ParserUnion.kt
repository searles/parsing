package at.searles.parsing.parser.combinators

import at.searles.parsing.parser.Parser
import at.searles.parsing.parser.ParserResult
import at.searles.parsing.parser.ParserStream
import at.searles.parsing.printer.PrintTree

class ParserUnion<A>(private val parsers: List<Parser<A>>) : Parser<A> {
    override fun parse(stream: ParserStream): ParserResult<A> {
        for(parser in parsers) {
            val result = parser.parse(stream)
            if(result.isSuccess) {
                return result
            }
        }

        return ParserResult.failure
    }

    override fun print(value: A): PrintTree {
        for(parser in parsers) {
            val result = parser.print(value)

            if(result.isSuccess) {
                return result
            }
        }

        return PrintTree.failure
    }

    override infix fun or(other: Parser<A>): Parser<A> {
        return ParserUnion(parsers + other)
    }

    override fun or(other: Parser<A>, swapPrint: Boolean): Parser<A> {
        return if(swapPrint) ParserUnion(listOf(other) + parsers) else this or other
    }
}
