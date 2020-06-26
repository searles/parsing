package at.searles.regexp

interface Visitor<A> {
    fun visitOr(l: Regexp, r: Regexp): A
    fun visitThen(l: Regexp, r: Regexp): A
    fun visitNonGreedy(regexp: Regexp): A

    fun visitRep(regexp: Regexp): A
    fun visitRep1(regexp: Regexp): A
    fun visitOpt(regexp: Regexp): A

    /**
     * Use code points!
     */
    fun visitText(string: String?): A
    fun visitRepRange(regexp: Regexp?, min: Int, max: Int): A
    fun visitRepCount(regexp: Regexp?, count: Int): A
    fun visitRepMin(regexp: Regexp?, min: Int): A
    fun visitEmpty(): A
    fun visitCharSet(set: CharSet?): A
}