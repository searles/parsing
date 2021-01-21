package at.searles.parsing.parser.combinators

import at.searles.parsing.parser.Parser
import at.searles.parsing.parser.ParserResult
import at.searles.parsing.parser.ParserStream
import at.searles.parsing.parser.Recognizer
import at.searles.parsing.printer.PrintTree

@Deprecated("use + CreateValue")
class InitValueParser<A>(private val recognizer: Recognizer, private val value: A) : Parser<A> {
    override fun parse(stream: ParserStream): ParserResult<A> {
        val result = recognizer.parse(stream)

        if(!result.isSuccess) return ParserResult.failure

        return ParserResult.of(value, result.index, result.length)
    }

    override fun print(value: A): PrintTree {
        if(value != this.value) {
            return PrintTree.failure
        }

        return recognizer.print()
    }
}
