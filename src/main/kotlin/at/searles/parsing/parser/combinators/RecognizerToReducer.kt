package at.searles.parsing.parser.combinators

import at.searles.parsing.lexer.TokenStream
import at.searles.parsing.parser.ParserResult
import at.searles.parsing.parser.Recognizer
import at.searles.parsing.parser.Reducer
import at.searles.parsing.printer.PartialPrintTree

class RecognizerToReducer<A>(private val recognizer: Recognizer) : Reducer<A, A> {
    override fun parse(stream: TokenStream, input: A): ParserResult<A> {
        val result = recognizer.parse(stream)

        return if(result.isSuccess) {
            ParserResult.of(input, result.index, result.length)
        } else {
            ParserResult.failure
        }
    }

    override fun print(value: A): PartialPrintTree<A> {
        return PartialPrintTree.of(value, recognizer.print())
    }

    override fun toString(): String {
        return recognizer.toString()
    }
}
