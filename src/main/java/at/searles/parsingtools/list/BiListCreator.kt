package at.searles.parsingtools.list

import at.searles.parsing.Fold
import at.searles.parsing.ParserStream

class BiListCreator<T> : Fold<T, T, List<T>> {
    override fun apply(stream: ParserStream, left: T, right: T): List<T> {
        return listOf(left, right)
    }

    override fun leftInverse(result: List<T>): T? {
        return if (result.size == 2) result[0] else null
    }

    override fun rightInverse(result: List<T>): T? {
        return if (result.size == 2) result[1] else null
    }

    override fun toString(): String {
        return "{list(x,y)}"
    }
}
