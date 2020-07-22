package at.searles.parsingtools.common

import at.searles.parsing.Fold
import at.searles.parsing.ParserStream

class SwappedPairCreator<T, U> : Fold<T, U, Pair<U, T>> {
    override fun apply(stream: ParserStream, left: T, right: U): Pair<U, T> {
        return Pair(right, left)
    }

    override fun leftInverse(result: Pair<U, T>): T? {
        return result.second
    }

    override fun rightInverse(result: Pair<U, T>): U? {
        return result.first
    }

    override fun toString(): String {
        return "{<y,x>}"
    }
}
