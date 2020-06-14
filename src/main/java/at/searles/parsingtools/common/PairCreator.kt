package at.searles.parsingtools.common

import at.searles.parsing.Fold
import at.searles.parsing.ParserStream
import at.searles.utils.Pair

class PairCreator<T, U> : Fold<T, U, Pair<T, U>> {
    override fun apply(stream: ParserStream, left: T, right: U): Pair<T, U> {
        return Pair(left, right)
    }

    override fun leftInverse(result: Pair<T, U>): T? {
        return result.l()
    }

    override fun rightInverse(result: Pair<T, U>): U? {
        return result.r()
    }

    override fun toString(): String {
        return "{<x,y>}"
    }
}
