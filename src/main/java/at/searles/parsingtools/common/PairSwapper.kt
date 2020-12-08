package at.searles.parsingtools.common

import at.searles.parsing.Mapping
import at.searles.parsing.ParserStream

class PairSwapper<T, U> : Mapping<Pair<T, U>, Pair<U, T>> {
    override fun toString(): String {
        return "{<x,y> -> <y,x>}"
    }

    override fun parse(left: Pair<T, U>, stream: ParserStream): Pair<U, T> {
        return Pair(left.second, left.first)
    }

    override fun left(result: Pair<U, T>): Pair<T, U>? {
        return Pair(result.second, result.first)
    }
}
