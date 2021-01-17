package at.searles.parsing.parser.combinators

import at.searles.parsing.parser.ParserResult
import at.searles.parsing.parser.ParserStream
import at.searles.parsing.parser.Recognizer
import at.searles.parsing.parser.Reducer
import at.searles.parsing.printer.PartialPrintResult

class RecognizerToReducer<A>(private val recognizer: Recognizer) : Reducer<A, A> {
    override fun parse(stream: ParserStream, input: A): ParserResult<A> {
        val result = recognizer.parse(stream)

        return if(result.isSuccess) {
            ParserResult.success(input, result.index, result.length)
        } else {
            ParserResult.failure()
        }
    }

    override fun print(value: A): PartialPrintResult<A> {
        TODO("Not yet implemented")
    }


}
