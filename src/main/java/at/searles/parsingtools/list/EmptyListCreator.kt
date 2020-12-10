package at.searles.parsingtools.list

import at.searles.parsing.Initializer
import at.searles.parsing.ParserStream

/**
 * Initializer that introduces an empty list
 */
class EmptyListCreator<T> : Initializer<List<T>> {
    override fun parse(stream: ParserStream): List<T> {
        return emptyList()
    }

    override fun consume(t: List<T>): Boolean {
        return t.isEmpty()
    }

    override fun toString(): String {
        return "{emptylist}"
    }
}
