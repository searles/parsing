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
            return chars(*chars.map { it.toInt() }.toIntArray())
        }

        fun chars(vararg codePoints: Int): CharSet {
            Arrays.sort(codePoints) // sort is not needed but speeds things up
            return chars(codePoints.asIterable())
        }

        fun ichars(vararg chars: Char): CharSet {
            val list = ArrayList<Int>(chars.size * 2)

            for(ch in chars) {
                list.add(ch.toInt())
                when(Character.getType(ch)) {
                    Character.LOWERCASE_LETTER.toInt() -> list.add(ch.toUpperCase().toInt())
                    Character.UPPERCASE_LETTER.toInt() -> list.add(ch.toLowerCase().toInt())
                }
            }

            list.sort()

            return chars(list)
        }

        fun chars(chars: Iterable<Int>): CharSet {
            val set = IntervalSet()

            chars.forEach {
                set.add(Interval(it))
            }

            return CharSet(set)
        }

        fun interval(vararg intervals: CharRange): CharSet {
            val set = IntervalSet()

            for(interval in intervals) {
                set.add(Interval(interval.first.toInt() .. interval.last.toInt()))
            }

            return CharSet(set)
        }

        fun interval(vararg intervals: IntRange): CharSet {
            val set = IntervalSet()

            for(interval in intervals) {
                set.add(Interval(interval))
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