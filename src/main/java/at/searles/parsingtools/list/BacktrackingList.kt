package at.searles.parsingtools.list

import java.util.AbstractList
import java.util.ArrayList

/**
 * To avoid side effects when backtracking but at the same time avoid excessive copying,
 * this class wraps a mutable list into an immutable list that though supports backtracking
 * by using a pushBack-method.
 *
 * @param <E>
</E> */
internal class BacktrackingList<E> : AbstractList<E> {

    private val list: MutableList<E>
    override val size: Int

    private constructor(list: ArrayList<E>) {
        this.list = list
        this.size = list.size
    }

    private constructor(parent: BacktrackingList<E>, lastElement: E) {
        this.list = parent.list
        this.size = parent.size + 1

        this.list.add(lastElement)

        assert(this.list.size == this.size)
    }

    fun pushBack(element: E): BacktrackingList<E> {
        // remove all elements behind size.
        this.list.subList(size, list.size).clear()
        return BacktrackingList(this, element)
    }

    private fun rangeCheck(index: Int) {
        if (index >= size) {
            throw IndexOutOfBoundsException(String.format("Size is %d but index requested is %d", size, index))
        }
    }

    override fun get(index: Int): E {
        rangeCheck(index)
        return list[index]
    }

    companion object {
        fun <T> create(list: List<T>): BacktrackingList<T> {
            return if (list is BacktrackingList<*>) {
                // Fast lane for immutable lists.
                list as BacktrackingList<T>
            } else BacktrackingList(ArrayList(list))
        }
    }
}
