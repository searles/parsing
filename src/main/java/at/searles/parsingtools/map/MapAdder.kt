package at.searles.parsingtools.map

import at.searles.parsing.Fold
import at.searles.parsing.ParserStream

class MapAdder<K, V>(private val key: K) : Fold<Map<K, V>, V, Map<K, V>> {

    override fun apply(stream: ParserStream, left: Map<K, V>, right: V): Map<K, V> {
        return left + (key to right)
    }

    override fun leftInverse(result: Map<K, V>): Map<K, V>? {
        if (!result.containsKey(key)) {
            return null
        }

        return result - key
    }

    override fun rightInverse(result: Map<K, V>): V? {
        return result[key]
    }

    override fun toString(): String {
        return "{put $key}"
    }
}
