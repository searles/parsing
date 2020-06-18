package at.searles.parsingtools.common

import at.searles.parsing.Fold
import at.searles.parsing.ParserStream

class PairCreator<T, U> : Fold<T, U, Pair<T, U>> {
    override fun apply(stream: ParserStream, left: T, right: U): Pair<T, U> {
        return Pair(left, right)
    }

    override fun leftInverse(result: Pair<T, U>): T? {
        return result.first
    }

    override fun rightInverse(result: Pair<T, U>): U? {
        return result.second
    }

    override fun toString(): String {
        return "{<x,y>}"
    }
}
