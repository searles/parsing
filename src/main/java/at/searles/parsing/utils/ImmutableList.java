package at.searles.parsing.utils;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

/**
 * To avoid side effects when backtracking but at the same time avoid excessive copying,
 * this class wraps a mutable list into an immutable list that though supports backtracking
 * by using a pushBack-method.
 * @param <E>
 */
public class ImmutableList<E> extends AbstractList<E> {

    private final List<E> list;
    private final int size;

    public ImmutableList() {
        list = new ArrayList<>();
        size = 0;
    }

    private ImmutableList(List<E> list) {
        this.list = list;
        this.size = list.size();
    }

    private ImmutableList(ImmutableList<E> parent, E lastElement) {
        this.list = parent.list;
        this.size = parent.size + 1;

        this.list.add(lastElement);

        assert this.list.size() == this.size;
    }

    public static <T> ImmutableList<T> createFrom(List<T> list) {
        if(list instanceof ImmutableList) {
            return (ImmutableList<T>) list;
        }

        return new ImmutableList<>(list);
    }

    public ImmutableList<E> pushBack(E element) {
        if(element == null) {
            throw new NullPointerException();
        }

        // remove all elements behind size.
        this.list.subList(size, list.size()).clear();

        return new ImmutableList<>(this, element);
    }

    private void rangeCheck(int index) {
        if (index >= size)
            throw new IndexOutOfBoundsException(String.format("Size is %d but index requested is %d", size, index));
    }

    @Override
    public E get(int index) {
        rangeCheck(index);
        return list.get(index);
    }

    @Override
    public int size() {
        return size;
    }
}
