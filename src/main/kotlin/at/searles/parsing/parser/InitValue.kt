package at.searles.parsing.parser

import at.searles.parsing.printer.PrintTree

class InitValue<A>(value: () -> A): Parser<A> {

    private val value = value()

    override fun parse(stream: ParserStream): ParserResult<A> {
        return ParserResult.of(value, stream.index, 0)
    }

    override fun print(value: A): PrintTree {
        return if(value == this.value) PrintTree.empty else PrintTree.failure
    }
}