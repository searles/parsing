package at.searles.parsing.lexer.regexp

import at.searles.parsing.lexer.fsa.IntervalSet
import java.util.*

class CharSet private constructor(private val set: IntervalSet) : Regexp, Iterable<IntRange> {
    constructor(vararg chars: Char): this(*chars.map { it.toInt() }.toIntArray())

    constructor(vararg codePoints: Int): this(codePoints.asIterable())

    constructor(chars: Iterable<Int>): this(IntervalSet().apply {
        chars.forEach {
            add((it .. it))
        }
    })

    constructor(vararg intervals: CharRange): this(IntervalSet().apply {
        for (interval in intervals) {
            add((interval.first.toInt()..interval.last.toInt()))
        }
    })

    constructor(vararg intervals: IntRange): this(IntervalSet().apply {
        for (interval in intervals) {
            add((interval))
        }
    })

    operator fun plus(that: CharSet): CharSet {
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

    operator fun contains(ch: Char): Boolean {
        return set.contains(ch.toInt())
    }

    override fun iterator(): Iterator<IntRange> {
        return set.iterator()
    }

    companion object {
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

            return CharSet(list)
        }

        fun empty(): CharSet {
            return CharSet(IntervalSet())
        }

        fun all(): CharSet {
            return CharSet(IntervalSet().apply { add((0 until Int.MAX_VALUE)) }) // TODO make it full.
        }

        fun eof(): Regexp {
            return CharSet(-1)
        }
    }
}