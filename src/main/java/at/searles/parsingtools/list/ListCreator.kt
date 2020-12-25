package at.searles.parsingtools.list

import at.searles.parsing.Mapping
import at.searles.parsing.ParserStream
import java.util.*

/**
 * Created by searles on 31.03.19.
 */
class ListCreator<T> : Mapping<T, List<T>> {

    override fun reduce(left: T, stream: ParserStream): List<T> {
        val l = ArrayList<T>()
        l.add(left)
        return l
    }

    override fun left(result: List<T>): T? {
        return if (result.size == 1) result[0] else null
    }

    override fun toString(): String {
        return "{x -> [x]}"
    }
}
