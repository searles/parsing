package at.searles.lexer.utils;

public class Interval implements Comparable<Interval> {

    public final int start;  // start and end are public for optimizations
    public final int end;    // end is exclusive

    public Interval(int ch) {
        this.start = ch;
        this.end = ch + 1;
    }

    public Interval(int start, int end) {
        this.start = start;
        this.end = end;
    }

    @Override
    public int compareTo(Interval that) {
        // Lexical order
        int cmp = Integer.compare(this.start, that.start);

        return cmp != 0 ? cmp : Integer.compare(this.end, that.end);
    }

    @Override
    public boolean equals(Object o) {
        if (getClass() == o.getClass()) {
            return compareTo((Interval) o) == 0;
        }

        return false;
    }

    public int contains(int ch) {
        return ch < start ? -1 : ch >= end ? 1 : 0;
    }

    @Override
    public String toString() {
        return String.format("[%d, %d)", start, end);
    }

}