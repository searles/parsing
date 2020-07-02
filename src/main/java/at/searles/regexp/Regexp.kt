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
        return KleeneStar(this)
    }

    // A+
    fun rep1(): Regexp {
        return KleenePlus(this)
    }

    // A{min,max}
    fun range(min: Int, max: Int): Regexp {
        return Range(this, min, max)
    }

    // A{min,}
    fun min(min: Int): Regexp {
        return AtLeast(this, min)
    }

    // A{count}
    fun count(count: Int): Regexp {
        return Count(this, count)
    }

    // A^
    fun nonGreedy(): Regexp {
        return FirstMatch(this)
    }

    infix fun and(other: Regexp): Regexp {
        return Intersect(this, other)
    }

    operator fun minus(other: Regexp): Regexp {
        return Subtract(this, other)
    }
}