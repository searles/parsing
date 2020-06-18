package at.searles.regexp;

/**
 * Interface for regular expressions
 */
public interface Regexp {

    static Regexp eof() {
        return CharSet.chars(-1);
    }

    // 'abc'
    static Regexp text(String s) {
        return new Text(s);
    }

    <A> A accept(Visitor<A> visitor);

    // A B
    default Regexp then(Regexp that) {
        return new Concat(this, that);
    }

    // A | B
    default Regexp or(Regexp that) {
        return new Union(this, that);
    }

    // A?
    default Regexp opt() {
        return new Closure(this, true, false);
    }

    // A*
    default Regexp rep() {
        return new Closure(this, true, true);
    }

    // A+
    default Regexp plus() {
        return new Closure(this, false, true);
    }

    // A{min,max}
    default Regexp range(int min, int max) {
        return new RepRange(this, min, max);
    }

    // A{min,}
    default Regexp min(int min) {
        return new RepMin(this, min);
    }

    // A{count}
    default Regexp count(int count) {
        return new RepCount(this, count);
    }

    // A^
    default Regexp nonGreedy() {
        return new NonGreedy(this);
    }
}
