package at.searles.parsing.parser

import at.searles.parsing.printer.PrintTree

class InitValue<A>(value: () -> A): Initializer<A> {

    private val value = value()

    override fun initialize(): A {
        return value
    }

    override fun consume(value: A): Boolean {
        return this.value == value
    }
}