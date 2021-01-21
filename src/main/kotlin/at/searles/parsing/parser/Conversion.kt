package at.searles.parsing.parser

import at.searles.parsing.printer.PartialPrintTree
import at.searles.parsing.printer.PrintTree

interface Conversion<A, B>: Reducer<A, B> {
    override fun parse(stream: ParserStream, input: A): ParserResult<B> {
        return ParserResult.of(convert(input), stream.index, 0)
    }

    fun convert(value: A): B

    override fun print(value: B): PartialPrintTree<A> {
        val invertedValue = invert(value)

        if(!invertedValue.isSuccess) {
            return PartialPrintTree.failure
        }

        return PartialPrintTree.of(invertedValue.value, PrintTree.Empty)
    }

    fun invert(value: B): FnResult<A> = FnResult.failure
}