package at.searles.parsing.parser.combinators

import at.searles.parsing.parser.Parser
import at.searles.parsing.parser.ParserResult
import at.searles.parsing.parser.ParserStream
import at.searles.parsing.parser.Recognizer
import at.searles.parsing.printer.PrintResult

class RecognizerPlusParser<A>(private val left: Recognizer, private val right: Parser<A>) : Parser<A> {
    override fun parse(stream: ParserStream): ParserResult<A> {
        val leftResult = left.parse(stream)

        return if(leftResult.isSuccess) {
            right.parse(stream)
        } else {
            ParserResult.failure()
        }
    }

    override fun print(value: A): PrintResult {
        val rightResult = right.print(value)

        if(!rightResult.isSuccess) {
            return PrintResult.failure()
        }

        return PrintResult.success(left.output + rightResult.output)
    }
}
