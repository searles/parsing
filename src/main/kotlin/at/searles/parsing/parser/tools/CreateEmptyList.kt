package at.searles.parsing.parser.tools

import at.searles.parsing.parser.Initializer

class CreateEmptyList<A>: Initializer<List<A>> {
    override fun initialize(): List<A> {
        return emptyList()
    }

    override fun consume(value: List<A>): Boolean {
        return value.isEmpty()
    }
}