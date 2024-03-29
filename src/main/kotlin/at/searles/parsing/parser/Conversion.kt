package at.searles.parsing.parser

import at.searles.parsing.lexer.TokenStream
import at.searles.parsing.printer.PartialPrintTree
import at.searles.parsing.printer.PrintTree

fun interface Conversion<A, B> {
    fun convert(value: A): B
    fun invert(value: B): FnResult<A> = error("not invertible function")

    fun asReducer(): Reducer<A, B> {
        return object: Reducer<A, B> {
            override fun parse(stream: TokenStream, input: A): ParserResult<B> {
                return ParserResult.of(convert(input), stream.startIndex, 0)
            }

            override fun print(value: B): PartialPrintTree<A> {
                val inverse = invert(value)

                if(!inverse.isSuccess) {
                    return PartialPrintTree.failure
                }

                return PartialPrintTree.of(inverse.value, PrintTree.empty)
            }
        }
    }
}