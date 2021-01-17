package at.searles.parsing.parser

interface Fold<A, B, C> {
    fun fold(left: A, right: B): C
}