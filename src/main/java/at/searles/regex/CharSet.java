package at.searles.regex;

import at.searles.lexer.utils.Interval;
import at.searles.lexer.utils.IntervalSet;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Iterator;

public class CharSet implements Regex, Iterable<Interval> {

    public static CharSet chars(int...chars) {
        Arrays.sort(chars);

        IntervalSet<Void> set = new IntervalSet<>();

        for(int ch : chars) {
            set.add(ch, ch + 1, null, (a, b) -> null);
        }

        return new CharSet(set);
    }

    /**
     *
     * @param intervals [a, b], b is inclusive!
     * @return
     */
    public static CharSet interval(int...intervals) {
        if(intervals.length % 2 != 0) {
            throw new IllegalArgumentException("must have an even number of ranges");
        }

        IntervalSet<Void> set = new IntervalSet<>();

        for(int i = 0; i < intervals.length; i += 2) {
            set.add(intervals[i], intervals[i + 1] + 1, null, (a, b) -> null);
        }

        return new CharSet(set);
    }

    public static CharSet fromIntervals(Iterable<Interval> intervals) {
        CharSet set = empty();

        for(Interval i : intervals) {
            set.set.add(i.start, i.end, null, (a, b) -> null);
        }

        return set;
    }

    public static CharSet empty() {
        return new CharSet();
    }

    public static CharSet all() {
        return interval(Integer.MIN_VALUE, Integer.MAX_VALUE - 1);
    }

    private final IntervalSet<Void> set;

    private CharSet(IntervalSet<Void> set) {
        this.set = set;
    }

    private CharSet() {
        this(new IntervalSet<>());
    }

    public <A> IntervalSet<A> copyIntervalSet(A a) {
        return set.copy(ignore -> a);
    }

    public boolean isAll() {
        IntervalSet.Iter<Void> it = set.iterator();

        if(it.hasNext()) {
            it.next();

            return it.start() == Integer.MIN_VALUE && it.end() == Integer.MAX_VALUE;
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
        if(!set.isEmpty()) {
            IntervalSet.Iter<Void> it = set.iterator();
            it.next();

            return it.start() == Integer.MIN_VALUE;
        }

        return false;
    }

    public CharSet invert() {
        IntervalSet<Void> inverted = new IntervalSet<>();

        int start = Integer.MIN_VALUE;

        IntervalSet.Iter<Void> it = set.iterator();

        while(it.hasNext()) {
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
