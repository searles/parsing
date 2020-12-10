package at.searles.parsingtools.opt

import at.searles.parsing.Initializer
import at.searles.parsing.ParserStream
import java.util.*

class NoneCreator<T> : Initializer<Optional<T>> {
    override fun parse(stream: ParserStream): Optional<T> {
        return Optional.empty()
    }

    override fun consume(t: Optional<T>): Boolean {
        return !t.isPresent
    }

    override fun toString(): String {
        return "{none}"
    }
}
