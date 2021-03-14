package at.searles.parsing.parser.combinators

import at.searles.parsing.parser.*
import at.searles.parsing.printer.PrintTree

class InitializerPlusReducer<A, B>(private val initializer: Initializer<A>, private val reducer: Reducer<A, B>) : Parser<B> {
    override fun parse(stream: ParserStream): ParserResult<B> {
        val left = initializer.initialize()
        return reducer.parse(stream, left)
    }

    override fun print(value: B): PrintTree {
        val result = reducer.print(value)

        if(!result.isSuccess || !initializer.consume(result.leftValue)) {
            return PrintTree.failure
        }

        return result.rightTree
    }
}
