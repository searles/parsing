package at.searles.regexp;

import at.searles.lexer.utils.Interval;
import at.searles.lexer.utils.IntervalSet;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Iterator;

public class CharSet implements Regexp, Iterable<Interval> {

    private final IntervalSet<Void> set;

    private CharSet(IntervalSet<Void> set) {
        this.set = set;
    }

    private CharSet() {
        this(new IntervalSet<>());
    }

    public static CharSet chars(int... chars) {
        Arrays.sort(chars);

        IntervalSet<Void> set = new IntervalSet<>();

        for (int ch : chars) {
            set.add(ch, ch + 1, null, (a, b) -> null);
        }

        return new CharSet(set);
    }

    /**
     * @param intervals [a, b], b is inclusive!
     * @return The CharSet convaining the intervals provided
     */
    public static CharSet interval(int... intervals) {
        if (intervals.length % 2 != 0) {
            throw new IllegalArgumentException("must have an even number of ranges");
        }

        IntervalSet<Void> set = new IntervalSet<>();

        for (int i = 0; i < intervals.length; i += 2) {
            set.add(intervals[i], intervals[i + 1] + 1, null, (a, b) -> null);
        }

        return new CharSet(set);
    }

    public static CharSet empty() {
        return new CharSet();
    }

    /**
     * Recognizes all positive integers except for Integer.MAX_VALUE.
     */
    public static CharSet all() {
        return interval(0, Integer.MAX_VALUE - 1);
    }

    public <A> IntervalSet<A> copyIntervalSet(A a) {
        return set.copy(ignore -> a);
    }

    public boolean isAll() {
        IntervalSet.Iter<Void> it = set.iterator();

        if (it.hasNext()) {
            it.next();

            return it.start() == 0 && it.end() == Integer.MAX_VALUE;
        }

        return false;
    }

    public boolean isEmpty() {
        return set.isEmpty();
    }

    public CharSet union(CharSet that) {
        return new CharSet(new IntervalSet<>(set).add(that.set, (a, b) -> null));
    }

    public boolean isInverted() {
        if (!set.isEmpty()) {
            IntervalSet.Iter<Void> it = set.iterator();
            it.previous();

            return it.end() == Integer.MAX_VALUE;
        }

        return false;
    }

    public CharSet invert() {
        IntervalSet<Void> inverted = new IntervalSet<>();

        int start = 0;

        IntervalSet.Iter<Void> it = set.iterator();

        while (it.hasNext()) {
            it.next();
            inverted.add(start, it.start(), null, (a, b) -> null);
            start = it.end();
        }

        inverted.add(start, Integer.MAX_VALUE, null, (a, b) -> null);
        return new CharSet(inverted);
    }

    @Override
    public <A> A accept(Visitor<A> visitor) {
        return visitor.visitCharSet(this);
    }

    @Override
    public String toString() {
        return String.format("CharSet(%s)", set.toString());
    }

    public boolean contains(int ch) {
        return set.contains(ch);
    }

    @NotNull
    @Override
    public Iterator<Interval> iterator() {
        return new Iterator<Interval>() {

            final IntervalSet.Iter<Void> it = set.iterator();

            @Override
            public boolean hasNext() {
                return it.hasNext();
            }

            @Override
            public Interval next() {
                it.next();
                return new Interval(it.start(), it.end());
            }
        };
    }
}
