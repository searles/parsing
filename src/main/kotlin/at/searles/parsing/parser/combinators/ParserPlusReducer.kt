package at.searles.parsing.parser.combinators

import at.searles.parsing.parser.Parser
import at.searles.parsing.parser.ParserResult
import at.searles.parsing.parser.ParserStream
import at.searles.parsing.parser.Reducer

class ParserPlusReducer<A, B>(private val left: Parser<A>, private val right: Reducer<A, B>): Parser<B> {
    override fun parse(stream: ParserStream): ParserResult<B> {
        val leftResult = left.parse(stream)

        return if(leftResult.isSuccess) {
            right.reduce(stream, leftResult)
        } else {
            stream.createFail()
        }
    }
}