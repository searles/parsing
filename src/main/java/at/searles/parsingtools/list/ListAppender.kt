package at.searles.parsingtools.list

import at.searles.parsing.Fold
import at.searles.parsing.ParserStream

class ListAppender<T>(private val minSize: Int = 0) : Fold<List<T>, T, List<T>> {

    override fun apply(stream: ParserStream, left: List<T>, right: T): List<T> {
        return ImmutableList.create(left).pushBack(right)
    }

    private fun cannotInvert(list: List<T>): Boolean {
        return list.size <= minSize
    }

    override fun leftInverse(result: List<T>): List<T>? {
        return if (cannotInvert(result)) {
            null
        } else result.subList(0, result.size - 1)

    }

    override fun rightInverse(result: List<T>): T? {
        return if (cannotInvert(result)) {
            null
        } else result[result.size - 1]

    }

    override fun toString(): String {
        return "{append}"
    }
}
