package at.searles.parsing.lexer.regexp

import at.searles.parsing.lexer.fsa.Interval
import at.searles.parsing.lexer.fsa.IntervalSet
import java.util.*

class CharSet private constructor(private val set: IntervalSet) : Regexp, Iterable<Interval> {
    infix fun or(that: CharSet): CharSet {
        return CharSet(set.copy().apply { add(that.set) })
    }

    fun invert(): CharSet {
        return CharSet(set.inverted(0, Int.MAX_VALUE))
    }

    override fun <A> accept(visitor: Visitor<A>): A {
        return visitor.visitSet(set)
    }

    override fun toString(): String {
        return String.format("CharSet(%s)", set.toString())
    }

    operator fun contains(ch: Int): Boolean {
        return set.contains(ch)
    }

    override fun iterator(): Iterator<Interval> {
        return set.iterator()
    }

    companion object {
        fun chars(vararg chars: Char): CharSet {
            Arrays.sort(chars)
            return chars(chars.asIterable())
        }

        fun chars(chars: Iterable<Char>): CharSet {
            val set = IntervalSet()

            chars.forEach {
                set.add(Interval(it.toInt(), it.toInt() + 1))
            }

            return CharSet(set)
        }

        fun ichars(vararg chars: Char): CharSet {
            val ichars = chars.fold(emptyList<Char>()) {
                l, chr -> if(chr.toLowerCase() != chr.toUpperCase()) {
                    l + chr.toUpperCase() + chr.toLowerCase()
                } else {
                    l + chr
                }
            }.sorted()

            return chars(ichars)
        }

        fun chars(vararg ints: Int): CharSet {
            Arrays.sort(ints)
            val set = IntervalSet()

            ints.forEach {
                set.add(Interval(it, it + 1))
            }

            return CharSet(set)
        }

        fun interval(vararg intervals: Char): CharSet {
            return interval(*intervals.map { it.toInt() }.toIntArray())
        }

        /**
         * @param intervals [a, b], b is inclusive!
         * @return The CharSet convaining the intervals provided
         */
        fun interval(vararg intervals: Int): CharSet {
            require(intervals.size % 2 == 0) { "must have an even number of ranges" }
            val set = IntervalSet()

            var i = 0

            while (i < intervals.size) {
                set.add(Interval(intervals[i], intervals[i + 1] + 1))
                i += 2
            }

            return CharSet(set)
        }

        fun empty(): CharSet {
            return CharSet(IntervalSet())
        }

        fun all(): CharSet {
            return CharSet(IntervalSet().apply { add(Interval(0, Int.MAX_VALUE)) })
        }

        fun eof(): Regexp {
            return chars(-1)
        }
    }
}