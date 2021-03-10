package at.searles.parsing.parser.tools

import at.searles.parsing.parser.Initializer

class InitValue<A>(val value: A): Initializer<A> {
    override fun initialize(): A {
        return value
    }

    override fun consume(value: A): Boolean {
        return this.value == value
    }

    override fun toString(): String {
        return "{$value}"
    }
}