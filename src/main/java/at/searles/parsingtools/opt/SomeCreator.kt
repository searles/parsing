package at.searles.parsingtools.opt

import at.searles.parsing.Mapping
import at.searles.parsing.ParserStream

import java.util.Optional

class SomeCreator<T> : Mapping<T, Optional<T>> {
    override fun parse(stream: ParserStream, input: T): Optional<T> {
        return Optional.of(input)
    }

    override fun left(result: Optional<T>): T? {
        return result.orElse(null)
    }

    override fun toString(): String {
        return "{some}"
    }
}
