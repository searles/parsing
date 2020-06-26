package at.searles.lexer.utils

import kotlin.math.max
import kotlin.math.min

class IntervalSet: Iterable<Interval> {

    private val intervals = ArrayList<Interval>()

    val size get() = intervals.size

    fun add(other: IntervalSet) {
        other.forEach { add(it) }
    }

    fun add(interval: Interval) {
        var l = 0
        var r = size

        while(l != r) {
            val m = (l + r) / 2

        // is interval left of intervals[m]
            if(interval.end < intervals[m].start) {
                r = m
            } else if(intervals[m].end < interval.start) {
                l = m + 1
            } else {
                intervals[m] = Interval(min(interval.start, intervals[m].start), max(interval.end, intervals[m].end))
                mergeIfPossibleAt(m)
                return
            }
        }

        intervals.add(l, interval)
    }

    private fun mergeIfPossibleAt(index: Int) {
        if(canMergeWithRight(index)) {
            intervals[index] = Interval(intervals[index].start, intervals[index + 1].end)
            intervals.removeAt(index + 1)
        }

        if(canMergeWithLeft(index)) {
            intervals[index - 1] = Interval(intervals[index - 1].start, intervals[index].end)
            intervals.removeAt(index)
        }
    }

    private fun canMergeWithLeft(index: Int) = index > 0 && intervals[index - 1].end >= intervals[index].start

    private fun canMergeWithRight(index: Int) = index < intervals.size - 1 && intervals[index].end >= intervals[index + 1].start

    fun copy(): IntervalSet {
        return IntervalSet().also {
            it.intervals.addAll(this.intervals)
        }
    }

    /**
     * Invert for the range
     */
    fun inverted(rangeStart: Int, rangeEnd: Int): IntervalSet {
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
        var l = 0
        var r = size

        while(l != r) {
            val m = (l + r) / 2

            // is value left of intervals[m]
            if(value < intervals[m].start) {
                r = m
            } else if(intervals[m].end <= value) {
                l = m + 1
            } else {
                return true
            }
        }

        return false
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