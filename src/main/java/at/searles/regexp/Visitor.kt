package at.searles.regexp

import at.searles.lexer.utils.IntervalSet

interface Visitor<A> {
    fun visitOr(l: Regexp, r: Regexp): A
    fun visitThen(l: Regexp, r: Regexp): A
    fun visitFirstMatch(regexp: Regexp): A
    fun visitKleeneStar(regexp: Regexp): A
    fun visitKleenePlus(regexp: Regexp): A
    fun visitOpt(regexp: Regexp): A
    fun visitText(string: String): A
    fun visitEmpty(): A
    fun visitMinus(l: Regexp, r: Regexp): A
    fun visitAnd(l: Regexp, r: Regexp): A
    fun visitSet(set: IntervalSet): A
    fun visitAtLeast(regexp: Regexp, count: Int): A
    fun visitCount(regexp: Regexp, count: Int): A
    fun visitRange(regexp: Regexp, min: Int, max: Int): A
}