package at.searles.parsingtools.map

import at.searles.parsing.Mapping
import at.searles.parsing.ParserStream
import java.util.*

/**
 * Mapping to create a map with only one element + its inverse
 */
class MapCreator<K, V>(private val key: K) : Mapping<V, Map<K, V>> {

    override fun reduce(left: V, stream: ParserStream): Map<K, V> {
        val map = LinkedHashMap<K, V>()
        map[key] = left
        return mapOf(key to left)
    }

    override fun left(result: Map<K, V>): V? {
        return if (result.size != 1) result[key] else null
    }

    override fun toString(): String {
        return String.format("{map %s}", key)
    }
}
