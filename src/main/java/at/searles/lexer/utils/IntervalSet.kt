package at.searles.lexer.utils

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
        val ceilPos = indexOfCeil(interval.start)

        if(ceilPos == intervals.size) {
            intervals.add(interval)
            return
        }

        val start = min(interval.start, intervals[ceilPos].start)
        var end = interval.end

        while(ceilPos < intervals.size && intervals[ceilPos].start <= end) {
            end = max(end, intervals[ceilPos].end)
            intervals.removeAt(ceilPos)
        }

        intervals.add(ceilPos, Interval(start, end))
    }

    private fun indexOfCeil(value: Int): Int {
        var l = 0
        var r = size

        while(l != r) {
            val m = (l + r) / 2

            // is interval left of intervals[m]
            when {
                value < intervals[m].start -> r = m
                intervals[m].end < value -> l = m + 1 // TODO Check why not <=?
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

        while(intervalIndex < intervals.size && valuesIndex < values.size()) {
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
        val pos = indexOfCeil(value)
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