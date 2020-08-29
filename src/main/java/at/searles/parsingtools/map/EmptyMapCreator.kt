package at.searles.parsingtools.map

import at.searles.parsing.Initializer
import at.searles.parsing.ParserStream

class EmptyMapCreator<K, V> : Initializer<Map<K, V>> {

    override fun parse(stream: ParserStream): Map<K, V> {
        return mapOf()
    }

    override fun consume(t: Map<K, V>): Boolean {
        return t.isEmpty()
    }

    override fun toString(): String {
        return "{empty map}"
    }
}
