package at.searles.parsing.parser.combinators

import at.searles.parsing.parser.*
import at.searles.parsing.printer.PrintTree

class RecognizerPlusInit<A>(private val recognizer: Recognizer, private val init: Initializer<A>) : Parser<A> {
    override fun parse(stream: ParserStream): ParserResult<A> {
        val result = recognizer.parse(stream)

        if(!result.isSuccess) {
            return ParserResult.failure
        }

        return ParserResult.of(init.initialize(), result.index, result.length)
    }

    override fun print(value: A): PrintTree {
        if(!init.consume(value)) {
            return PrintTree.failure
        }

        return recognizer.print()
    }

    override fun toString(): String {
        return "$recognizer.plus($init)"
    }
}
