package at.searles.regexp

/**
 * Interface for regular expressions
 */
interface Regexp {
    fun <A> accept(visitor: Visitor<A>): A

    // A B
    operator fun plus(that: Regexp): Regexp {
        return Concat(this, that)
    }

    // A | B
    infix fun or(that: Regexp): Regexp {
        return Union(this, that)
    }

    // A?
    fun opt(): Regexp {
        return Opt(this)
    }

    // A*
    fun rep(): Regexp {
        return Rep(this)
    }

    // A+
    fun rep1(): Regexp {
        return Rep1(this)
    }

    // A{min,max}
    fun range(min: Int, max: Int): Regexp? {
        return RepRange(this, min, max)
    }

    // A{min,}
    fun min(min: Int): Regexp? {
        return RepMin(this, min)
    }

    // A{count}
    fun count(count: Int): Regexp? {
        return RepCount(this, count)
    }

    // A^
    fun nonGreedy(): Regexp? {
        return NonGreedy(this)
    }
}