package at.searles.parsing.parser

import at.searles.parsing.printer.PartialPrintResult

interface Reducer<A, B> {
    fun parse(stream: ParserStream, input: A): ParserResult<B>
    fun print(value: B): PartialPrintResult<A>
}