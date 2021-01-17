package at.searles.parsing.parser.combinators

import at.searles.parsing.parser.ParserResult
import at.searles.parsing.parser.ParserStream
import at.searles.parsing.parser.Recognizer
import at.searles.parsing.parser.Reducer

class RecognizerToReducer<A>(private val recognizer: Recognizer) : Reducer<A, A> {
    override fun reduce(stream: ParserStream, left: ParserResult<A>): ParserResult<A> {
        val result = recognizer.parse(stream)

        return if(result.isSuccess) {
            left
        } else {
            stream.createFail()
        }
    }

}
