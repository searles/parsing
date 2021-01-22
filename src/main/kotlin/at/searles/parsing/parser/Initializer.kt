package at.searles.parsing.parser

import at.searles.parsing.printer.PrintTree

interface Initializer<A>: Parser<A> {
    override fun parse(stream: ParserStream): ParserResult<A> {
        return ParserResult.of(initialize(), stream.index, 0L)
    }

    override fun print(value: A): PrintTree {
        if(!consume(value)) {
            return PrintTree.failure
        }

        return PrintTree.empty
    }

    fun initialize(): A
    fun consume(value: A): Boolean
}