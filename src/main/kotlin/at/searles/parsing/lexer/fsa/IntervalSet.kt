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
        val pos = indexOfOverlapOrCeil(interval.start - 1)

        if(pos == intervals.size) {
            intervals.add(interval)
            return
        }

        val start = min(interval.start, intervals[pos].start)
        var end = interval.end

        while(pos < intervals.size && intervals[pos].start <= end) {
            end = max(end, intervals[pos].end)
            intervals.removeAt(pos)
        }

        intervals.add(pos, Interval(start, end))
    }

    private fun indexOfOverlapOrCeil(value: Int): Int {
        var l = 0
        var r = size

        while(l != r) {
            val m = (l + r) / 2

            // is interval left of intervals[m]
            when {
                value < intervals[m].start -> r = m
                intervals[m].end <= value -> l = m + 1
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
            if(start < it.start) {
                invertedSet.add(Interval(start, it.start))
                start = it.end
            }
        }

        if(start <= rangeEnd) {
            invertedSet.add(Interval(start, rangeEnd))
        }

        return invertedSet
    }

    fun containsAny(values: IntSet): Boolean {
        var intervalIndex = 0
        var valuesIndex = 0

        while(intervalIndex < intervals.size && valuesIndex < values.size) {
            val interval = intervals[intervalIndex]
            val value = values[valuesIndex]

            when {
                interval.end <= value -> intervalIndex++
                value < interval.start -> valuesIndex++
                else -> return true
            }
        }

        return false
    }

    fun contains(value: Int): Boolean {
        val pos = indexOfOverlapOrCeil(value)
        return pos < intervals.size && intervals[pos].contains(value)
    }

    fun containsAny(other: IntervalSet): Boolean {
        var i0 = 0
        var i1 = 0
        while(i0 < this.size && i1 < other.size) {
            when {
                intervals[i0].end <= other.intervals[i1].start -> i0++
                other.intervals[i1].end <= intervals[i0].start -> i1++
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