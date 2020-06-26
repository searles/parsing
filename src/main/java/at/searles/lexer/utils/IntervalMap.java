package at.searles.lexer.utils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;

/**
 * Set of elements
 */
public class IntervalMap<A> implements Iterable<A> {
    /**
     * There are no conflicts in
     */
    protected final ArrayList<Entry<A>> list;

    public IntervalMap() {
        // make things easier
        list = new ArrayList<>(2);
    }

    public IntervalMap(IntervalMap<A> that) {
        // make things easier
        list = new ArrayList<>(that.list);
    }

    public <B> IntervalMap(IntervalMap<B> that, MapFn<B, A> mapFn) {
        list = new ArrayList<>(that.list.size());

        Iter<B> it = that.iterator();

        while (it.hasNext()) {
            it.next();
            this.insertAt(list.size(), it.start(), it.end(), mapFn.apply(it.value()));
        }

    }

    public <B> IntervalMap<B> copy(MapFn<A, B> mapFn) {
        return new IntervalMap<>(this, mapFn);
    }

    public void clear() {
        list.clear();
    }

    @NotNull
    public Iter<A> iterator() {
        return new Iter<>(this);
    }

    public boolean isEmpty() {
        return list.isEmpty();
    }

    @Override
    public boolean equals(Object that) {
        if (this != that && that != null && this.getClass() == that.getClass()) {
            return this.list.equals(((IntervalMap<?>) that).list);
        }

        return this == that;
    }

    public int hashCode() {
        return this.list.hashCode();
    }

    private Entry<A> findStart(int pos) {
        int index = findSmallerEq(pos, 0, list.size() - 1);

        if (0 <= index) {
            Entry<A> entry = list.get(index);
            if (entry.isStart) {
                return entry;
            }
        }

        return null;
    }

    public boolean contains(int pos) {
        return findStart(pos) != null;
    }

    public A find(int pos) {
        Entry<A> e = findStart(pos);
        return e == null ? null : e.value;
    }

    // when I look for an element, I must find the
    // first start smaller than pos.
    // then, its end must be right next to it.

    public IntervalMap<A> add(IntervalMap<A> that, MergeFn<A> merger) {
        for (int i = 0; i < that.list.size(); i += 2) {
            Entry<A> start = that.list.get(i);
            Entry<A> end = that.list.get(i + 1);

            add(start.position, end.position, start.value, merger);
        }

        return this;
    }

    /**
     * Returns the index of the first entry that
     * is smaller or equal to pos. Binary search.
     *
     * @return -1 if none is found
     */
    private int findSmallerEq(int pos, int l, int r) {
        if (r < l) {
            return -1;
        }

        if (l == r) {
            return list.get(l).position <= pos ? l : -1;
        }

        int m = (l + (r - l) / 2);

        Entry<A> e = list.get(m);

        if (pos < e.position) {
            return findSmallerEq(pos, l, m);
        } else {
            int ret = findSmallerEq(pos, m + 1, r);
            return ret == -1 ? m : ret;
        }
    }

    private int findStartInsertIndex(int startPos) {
        if (list.isEmpty()) {
            return 0;
        } else {
            int index = findSmallerEq(startPos, 0, list.size() - 1);
            if (index != -1 && list.get(index).position == startPos && list.get(index).isStart) {
                return index;
            }

            return index + 1;
        }
    }

    public IntervalMap<A> add(int start, int end, A value, MergeFn<A> merger) {
        int index = findStartInsertIndex(start);

        // list.get(index).position <= start
        // start < and list.get(index + 1).position
        if (0 < index && list.get(index - 1).isStart) {
            Entry<A> startEntry = list.get(index - 1);
            Entry<A> endEntry = list.get(index);

            int endEntryPosition = endEntry.position;
            endEntry.position = start;

            // now there is a gap from start to min(end, after.position)

            A merged = merger.apply(value, startEntry.value);

            if (endEntryPosition < end) {
                add(endEntryPosition, end, value, merger);
            } else if (end < endEntryPosition) {
                add(end, endEntryPosition, startEntry.value, merger);
            }

            add(start, Math.min(end, endEntryPosition), merged, merger);

            return this;
        }

        if (index < list.size() && list.get(index).position < end) {
            Entry<A> startEntry = list.get(index);
            Entry<A> endEntry = list.get(index + 1);

            int startEntryPosition = startEntry.position;

            A merged = merger.apply(value, startEntry.value);

            if (end < endEntry.position) {
                startEntry.position = end;
            } else {
                list.subList(index, index + 2).clear();

                if (endEntry.position < end) {
                    // exclude case where ends are aligned.
                    add(endEntry.position, end, value, merger);
                }
            }

            add(startEntryPosition, Math.min(end, endEntry.position), merged, merger);

            if (start < startEntryPosition) {
                add(start, startEntryPosition, value, merger);
            }

            return this;
        }

        insertAt(index, start, end, value);

        return this;
    }

    private void insertAt(int index, int start, int end, A value) {
        boolean matchBack = (index < list.size() - 1 && list.get(index).position == end && Objects.equals(list.get(index).value, value));
        boolean matchFront = (0 < index && list.get(index - 1).position == start && Objects.equals(list.get(index - 1).value, value));

        // the order of removes/adds is important.

        if (matchBack) {
            list.remove(index); // remove start of next interval
        } else {
            list.add(index, new Entry<>(end, false, value)); // add end for this interval
        }

        if (matchFront) {
            list.remove(index - 1); // remove end of last interval
        } else {
            list.add(index, new Entry<>(start, true, value)); // add start of this interval
        }
    }

    private String intervalString(int index) {
        Entry<A> startEntry = list.get(index);
        Entry<A> endEntry = list.get(index + 1);
        return "[" + startEntry.position + ", " + endEntry.position + ") -> " + startEntry.value;
    }

    public String toString() {
        if (list.isEmpty()) {
            return "{}";
        } else {
            StringBuilder sb = new StringBuilder("{").append(intervalString(0));
            for (int i = 2; i < list.size(); i += 2) {
                sb.append(", ").append(intervalString(i));
            }

            return sb.append("}").toString();
        }
    }

    public interface MapFn<A, B> {
        B apply(A a);
    }

    public interface MergeFn<A> {
        A apply(A arg0, A arg1);
    }

    private final static class Entry<A> {
        final boolean isStart;
        int position;
        A value;

        Entry(int position, boolean isStart, A value) {
            this.position = position;
            this.isStart = isStart;
            this.value = value;
        }

        public String toString() {
            return (isStart ? "s" : "e") + position + ": " + value;
        }

        @Override
        public int hashCode() {
            return value.hashCode() + 2 * position + (isStart ? 0 : 1);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            } else if (obj == null || getClass() != obj.getClass()) {
                return false;
            }

            Entry<?> other = (Entry<?>) obj;

            return isStart == other.isStart && position == other.position && Objects.equals(value, other.value);
        }

    }

    public static class Iter<A> implements Iterator<A> {
        private final IntervalMap<A> set;
        private int index = 0;

        Iter(IntervalMap<A> set) {
            this.set = set;
        }

        public boolean hasNext() {
            return index < set.list.size();
        }

        public boolean hasPrevious() {
            return index > 2;
        }

        public A next() {
            index += 2;
            return value();
        }

        public A previous() {
            if(index == 0) {
                index = set.list.size();
            } else {
                index -= 2;
            }

            return value();
        }

        public int start() {
            return set.list.get(index - 2).position;
        }

        public int end() {
            return set.list.get(index - 1).position;
        }

        public A value() {
            return set.list.get(index - 2).value;
        }

        public void setValue(A newValue) {
            set.list.get(index - 2).value = newValue;

            // merge if possible.
            if (hasPrevious()) {
                if (set.list.get(index - 3).position == set.list.get(index - 2).position &&
                        Objects.equals(set.list.get(index - 3).value, newValue)) {
                    // and it can be merged
                    set.list.get(index - 3).position = set.list.get(index - 1).position;
                    set.list.subList(index - 2, index).clear();
                    index -= 2;
                }
            }

            if (hasNext()) {
                if (set.list.get(index - 1).position == set.list.get(index).position &&
                        Objects.equals(set.list.get(index).value, newValue)) {
                    // and it can be merged
                    set.list.get(index - 1).position = set.list.get(index + 1).position;
                    set.list.subList(index, index + 2).clear();
                }
            }
        }
    }
}