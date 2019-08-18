package at.searles.lexer.utils;

import java.util.Arrays;

public class IntSet {

    private int[] elements;
    private int size;

    public IntSet(int size) {
        elements = new int[size];
        this.size = 0;
    }

    public IntSet() {
        this(16);
    }

    public int getAt(int index) {
        return elements[index];
    }

    private void ensureSize(int requiredSize) {
        if (elements.length < requiredSize) {
            int newSize = elements.length;

            while (newSize < requiredSize) {
                newSize *= 2;
            }

            int[] newElements = new int[newSize];
            System.arraycopy(elements, 0, newElements, 0, elements.length);
            this.elements = newElements;
        }
    }

    public boolean add(int item) {
        int index = Arrays.binarySearch(elements, 0, size, item);

        if (index >= 0) {
            // it exists.
            return false;
        }

        ensureSize(size + 1);

        // index == (-(insertion point) - 1)
        int insertionPoint = -(index + 1);

        // move all by one behind.
        System.arraycopy(elements, insertionPoint, elements, insertionPoint + 1, size - insertionPoint);

        elements[insertionPoint] = item;
        size++;

        return true;
    }

    public int size() {
        return this.size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public void addAll(IntSet other) {
        // XXX could be faster...
        for (int i = 0; i < other.size(); ++i) {
            this.add(other.getAt(i));
        }
    }

    public boolean contains(int item) {
        return Arrays.binarySearch(elements, 0, size(), item) >= 0;
    }

    public void retainAll(IntSet other) {
        // XXX can be made faster
        for (int i = size() - 1; i >= 0; --i) {
            if (!other.contains(getAt(i))) {
                removeAt(i);
            }
        }
    }

    public void removeAll(IntSet other) {
        // XXX can be made faster
        for (int i = 0; i < other.size(); ++i) {
            remove(other.getAt(i));
        }
    }

    public void removeAt(int index) {
        System.arraycopy(elements, index + 1, elements, index, size - 1 - index);
        this.size--;
    }

    public boolean remove(int item) {
        int index = Arrays.binarySearch(elements, 0, size, item);

        if (index < 0) {
            return false;
        }

        removeAt(index);

        return true;
    }

    public boolean containsAny(IntSet other) {
        int i0 = 0;
        int i1 = 0;

        while (i0 < this.size() && i1 < other.size()) {
            if (getAt(i0) < other.getAt(i1)) {
                i0++;
            } else if (getAt(i0) > other.getAt(i1)) {
                i1++;
            } else {
                return true;
            }
        }

        return false;
    }

    public int indexOfFirstMatch(IntSet other) {
        int i0 = 0;
        int i1 = 0;

        while (i0 < this.size() && i1 < other.size()) {
            if (getAt(i0) < other.getAt(i1)) {
                i0++;
            } else if (getAt(i0) > other.getAt(i1)) {
                i1++;
            } else {
                return i0;
            }
        }

        return -1;
    }

    public int last() {
        return elements[size - 1];
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("{");

        for (int i = 0; i < size; ++i) {
            if (i > 0) {
                sb.append(", ");
            }

            sb.append(getAt(i));
        }

        sb.append("}");

        return sb.toString();
    }
}
