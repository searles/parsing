package at.searles.parsing.lexer.fsa

import kotlin.math.max
import kotlin.math.min

/**
 * Format is start - end - start - end ...
 */
class IntervalSet(vararg intervals: Interval) : Iterable<Interval> {

    private val intervals = ArrayList<Interval>()

    init {
        intervals.forEach {
            this.intervals.add(it)
        }
    }

    val size get() = intervals.size

    fun add(other: IntervalSet) {
        other.forEach { add(it) }
    }

    fun add(interval: Interval) {
        // -1 because touch is fine.
        val pos = indexOfOverlapOrCeil(interval.first - 1)

        if(pos == intervals.size) {
            intervals.add(interval)
            return
        }

        val start = min(interval.first, intervals[pos].first)
        var last = interval.last

        while(pos < intervals.size && intervals[pos].first - 1 <= last) {
            last = max(last, intervals[pos].last)
            intervals.removeAt(pos)
        }

        intervals.add(pos, Interval(start .. last))
    }

    private fun indexOfOverlapOrCeil(value: Int): Int {
        var l = 0
        var r = size

        while(l != r) {
            val m = (l + r) / 2

            // is interval left of intervals[m]
            when {
                value < intervals[m].first -> r = m
                intervals[m].last < value -> l = m + 1
                else -> return m
            }
        }

        return l
    }

    fun copy(): IntervalSet {
        return IntervalSet().also {
            it.intervals.addAll(this.intervals)
        }
    }

    /**
     * Invert for the range
     */
    fun inverted(rangeStart: Int = Integer.MIN_VALUE, rangeEnd: Int = Integer.MAX_VALUE): IntervalSet {
        var start = rangeStart

        val invertedSet = IntervalSet()

        intervals.forEach {
            if(start < it.first) {
                invertedSet.add(Interval(start, it.first))
                start = it.last + 1
            }
        }

        if(start <= rangeEnd) {
            invertedSet.add(Interval(start, rangeEnd))
        }

        return invertedSet
    }

    fun contains(value: Int): Boolean {
        val pos = indexOfOverlapOrCeil(value)
        return pos < intervals.size && value in intervals[pos]
    }

    fun containsAny(other: IntervalSet): Boolean {
        var i0 = 0
        var i1 = 0
        while(i0 < this.size && i1 < other.size) {
            when {
                intervals[i0].last < other.intervals[i1].first -> i0++
                other.intervals[i1].last < intervals[i0].first -> i1++
                else -> return true
            }
        }
        return false
    }

    override fun iterator(): Iterator<Interval> {
        return intervals.iterator()
    }

    override fun toString(): String {
        return intervals.toString()
    }
}