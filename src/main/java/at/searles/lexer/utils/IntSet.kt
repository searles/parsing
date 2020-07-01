package at.searles.lexer.utils

import java.util.*
import kotlin.math.min

class IntSet(size: Int = 16): Iterable<Int>, Comparable<IntSet> {

    private var elements = IntArray(size)
    
    var size: Int = 0
        private set

    operator fun get(index: Int): Int {
        return elements[index]
    }

    private fun ensureSize(requiredSize: Int) {
        if (elements.size < requiredSize) {
            var newSize = elements.size
            while (newSize < requiredSize) {
                newSize *= 2
            }
            val newElements = IntArray(newSize)
            System.arraycopy(elements, 0, newElements, 0, elements.size)
            elements = newElements
        }
    }

    fun add(item: Int): Boolean {
        val index = Arrays.binarySearch(elements, 0, size, item)
        if (index >= 0) {
            // it exists.
            return false
        }
        ensureSize(size + 1)

        // index == (-(insertion point) - 1)
        val insertionPoint = -(index + 1)

        // move all by one behind.
        System.arraycopy(elements, insertionPoint, elements, insertionPoint + 1, size - insertionPoint)
        elements[insertionPoint] = item
        size++
        return true
    }

    val isEmpty: Boolean
        get() = size == 0

    fun addAll(other: IntSet) {
        for (i in 0 until other.size) {
            add(other[i])
        }
    }

    operator fun contains(item: Int): Boolean {
        return Arrays.binarySearch(elements, 0, size, item) >= 0
    }

    fun retainAll(other: IntSet) {
        for (i in size - 1 downTo 0) {
            if (!other.contains(get(i))) {
                removeAt(i)
            }
        }
    }

    fun removeAll(other: IntSet) {
        for (i in 0 until other.size) {
            remove(other[i])
        }
    }

    fun removeAt(index: Int) {
        System.arraycopy(elements, index + 1, elements, index, size - 1 - index)
        size--
    }

    fun remove(item: Int): Boolean {
        val index = Arrays.binarySearch(elements, 0, size, item)
        if (index < 0) {
            return false
        }
        removeAt(index)
        return true
    }

    fun containsAny(other: IntSet): Boolean {
        var i0 = 0
        var i1 = 0
        while (i0 < size && i1 < other.size) {
            if (get(i0) < other[i1]) {
                i0++
            } else if (get(i0) > other[i1]) {
                i1++
            } else {
                return true
            }
        }
        return false
    }

    fun indexOfFirstMatch(other: IntSet): Int {
        var i0 = 0
        var i1 = 0
        while (i0 < size && i1 < other.size) {
            if (get(i0) < other[i1]) {
                i0++
            } else if (get(i0) > other[i1]) {
                i1++
            } else {
                return i0
            }
        }
        return -1
    }

    fun last(): Int {
        return elements[size - 1]
    }

    fun clear() {
        size = 0
    }

    override fun compareTo(other: IntSet): Int {
        repeat(min(size, other.size)) {
            val cmp = elements[it].compareTo(elementAt(it))

            if(cmp != 0) {
                return cmp
            }
        }

        return size.compareTo(other.size)
    }

    override fun toString(): String {
        return "{${this.joinToString(", ")}}"
    }


    override fun iterator(): Iterator<Int> {
        return object: Iterator<Int>{
            var index = 0

            override fun hasNext(): Boolean {
                return index < size
            }

            override fun next(): Int {
                return this@IntSet[index++]
            }
        }
    }
}