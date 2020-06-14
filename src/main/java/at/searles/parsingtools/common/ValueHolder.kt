package at.searles.parsingtools.common

import at.searles.parsing.Initializer
import at.searles.parsing.ParserStream

class ValueHolder<T>(private val value: T) : Initializer<T> {

    override fun parse(stream: ParserStream): T {
        return value
    }

    override fun consume(t: T): Boolean {
        return t == value
    }

    override fun toString(): String {
        return "{$value}"
    }
}
