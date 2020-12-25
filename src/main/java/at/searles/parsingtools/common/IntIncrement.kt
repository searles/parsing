package at.searles.parsingtools.common

import at.searles.parsing.Mapping
import at.searles.parsing.ParserStream

class IntIncrement(private val min: Int) : Mapping<Int, Int> {
    override fun reduce(left: Int, stream: ParserStream): Int {
        return left + 1
    }

    override fun left(result: Int): Int? {
        return if (result > min) result - 1 else null
    }

    override fun toString(): String {
        return "{x -> x + 1}"
    }
}
