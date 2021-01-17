package at.searles.parsing.parser.combinators

import at.searles.parsing.parser.Parser
import at.searles.parsing.parser.ParserResult
import at.searles.parsing.parser.ParserStream
import at.searles.parsing.parser.Recognizer

class RecognizerPlusParser<A>(private val left: Recognizer, private val right: Parser<A>) : Parser<A> {
    override fun parse(stream: ParserStream): ParserResult<A> {
        val leftResult = left.parse(stream)

        if(leftResult.isSuccess) {
            return right.parse(stream)
        } else {
            return stream.createFail()
        }
    }

}
