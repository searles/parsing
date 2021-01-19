package at.searles.parsing.lexer.fsa

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
        // Basic binary search
        var pos = indexOfOverlapOrCeil(interval.first)

        var start = interval.first
        val end = interval.last + 1

        while(start < end) {
            if(pos == entries.size) {
                entries.add(Entry(Interval(start, end), value))
                return
            }

            val entry = entries[pos]

            if (start < entry.interval.first) {
                val nextEnd = min(end, entry.interval.first)
                entries.add(pos, Entry(Interval(start, nextEnd), value))

                start = nextEnd
                pos++
            } else if (start == entry.interval.first) {
                if (end < entry.interval.last + 1) {
                    entries.add(pos, Entry(Interval(start, end), mergeFn(entry.value, value)))
                    entries[pos + 1] = Entry(Interval(end .. entry.interval.last), entry.value)
                    start = end
                } else {
                    entries[pos] = Entry(Interval(start .. entry.interval.last), mergeFn(entry.value, value))
                    start = entry.interval.last + 1
                    pos++
                }
            } else {
                require(start < entry.interval.last + 1) { "bug in binary search: $start, ${entry.interval}" }
                entries.add(pos, Entry(Interval(entry.interval.first, start), entry.value))
                entries[pos + 1] = Entry(Interval(start .. entry.interval.last), entry.value)
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
                value < entries[m].interval.first -> r = m
                entries[m].interval.last + 1 <= value -> l = m + 1
                else -> return m
            }
        }

        return l
    }

    operator fun get(value: Int): A? {
        val pos = indexOfOverlapOrCeil(value)

        return entries.getOrNull(pos)?.let {
            return if(value in it.interval) {
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