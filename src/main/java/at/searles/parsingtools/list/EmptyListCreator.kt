package at.searles.parsingtools.list

import at.searles.parsing.Initializer
import at.searles.parsing.ParserStream

/**
 * Initializer that introduces an empty list
 */
class EmptyListCreator<T> : Initializer<List<T>> {
    private object Holder {
        internal var instance: List<*> = emptyList<Any>()
    }

    override fun parse(stream: ParserStream): List<T> {
        @Suppress("UNCHECKED_CAST")
        return Holder.instance as List<T>
    }

    override fun consume(t: List<T>): Boolean {
        return t.isEmpty()
    }

    override fun toString(): String {
        return "{emptylist}"
    }
}
