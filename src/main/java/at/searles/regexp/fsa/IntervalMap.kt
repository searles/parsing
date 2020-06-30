package at.searles.regexp.fsa

import at.searles.lexer.utils.Interval
import kotlin.math.min

class IntervalMap<A>: MutableIterable<IntervalMap.Entry<A>> {
    private val entries = ArrayList<Entry<A>>()

    val size get() = entries.size
    val values get() = this.map { it.value }

    val isEmpty get() = entries.isEmpty()

    fun add(other: IntervalMap<A>, mergeFn: (A, A) -> A = { _, _ -> error("intersect") }) {
        other.entries.forEach { add(it.interval, it.value, mergeFn) }
    }

    fun add(interval: Interval, value: A, mergeFn: (A, A) -> A = { _, _ -> error("intersect") }) {
        var pos = indexOfOverlapOrCeil(interval.start)

        var start = interval.start
        val end = interval.end

        while(start < end) {
            if(pos == entries.size) {
                entries.add(Entry(Interval(start, end), value))
                return
            }

            val entry = entries[pos]

            if (start < entry.interval.start) {
                val nextEnd = min(end, entry.interval.start)
                entries.add(pos, Entry(Interval(start, nextEnd), value))

                start = nextEnd
                pos++
            } else if (start == entry.interval.start) {
                if (end < entry.interval.end) {
                    entries.add(pos, Entry(Interval(start, end), mergeFn(entry.value, value)))
                    entries[pos + 1] = Entry(Interval(end, entry.interval.end), entry.value)
                    start = end
                } else {
                    entries[pos] = Entry(Interval(start, entry.interval.end), mergeFn(entry.value, value))
                    start = entry.interval.end
                    pos++
                }
            } else {
                require(start < entry.interval.end) { "bug in binary search: $start, ${entry.interval}" }
                entries.add(pos, Entry(Interval(entry.interval.start, start), entry.value))
                entries[pos + 1] = Entry(Interval(start, entry.interval.end), entry.value)
                pos++
            }
        }
    }

    private fun indexOfOverlapOrCeil(value: Int): Int {
        var l = 0
        var r = size

        while(l != r) {
            val m = (l + r) / 2

            when {
                value < entries[m].interval.start -> r = m
                entries[m].interval.end <= value -> l = m + 1
                else -> return m
            }
        }

        return l
    }

    operator fun get(value: Int): A? {
        val pos = indexOfOverlapOrCeil(value)

        return entries.getOrNull(pos)?.let {
            return if(it.interval.contains(value)) {
                it.value
            } else {
                null
            }
        }
    }

    override fun toString(): String {
        return entries.joinToString(", ")
    }

    class Entry<A>(val interval: Interval, val value: A) {
        override fun toString(): String {
            return "$interval -> $value"
        }
    }

    override fun iterator(): MutableIterator<Entry<A>> {
        return entries.iterator()
    }

    fun clear() {
        entries.clear()
    }

    private fun addAll(newEntries: List<Entry<A>>) {
        entries.addAll(newEntries)
    }

    fun <B> mapValues(conversion: (A) -> B): IntervalMap<B> {
        val newEntries = entries.map { Entry(it.interval, conversion(it.value)) }

        return IntervalMap<B>().apply {
            addAll(newEntries)
        }
    }
}