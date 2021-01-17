package at.searles.parsing.parser

import at.searles.parsing.printer.PartialPrintResult

interface Conversion<A, B>: Reducer<A, B> {
    override fun parse(stream: ParserStream, input: A): ParserResult<B> {
        return ParserResult.success(convert(input), stream.index, 0)
    }

    fun convert(left: A): B

    override fun print(value: B): PartialPrintResult<A> {
        val invertedValue = invert(value)

        if(!invertedValue.isSuccess) {
            return PartialPrintResult.failure()
        }

        return PartialPrintResult.success(invertedValue.value, "")
    }

    fun invert(value: B): FnResult<A> = FnResult.failure()
}