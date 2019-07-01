package at.searles.regex;

/**
 * Interface for regular expressions
 */
public interface Regex {

    <A> A accept(Visitor<A> visitor);

    // 'abc'
    static Regex text(String s) {
        return new Text(s);
    }

    // A B
    default Regex then(Regex that) {
        return new Concat(this, that);
    }

    // A | B
    default Regex or(Regex that) {
        return new Alt(this, that);
    }

    // A?
    default Regex opt() {
        return new Closure(this, true, false);
    }

    // A*
    default Regex rep() {
        return new Closure(this, true, true);
    }

    // A+
    default Regex plus() {
        return new Closure(this, false, true);
    }

    // A{min,max}
    default Regex range(int min, int max) {
        return new RepRange(this, min, max);
    }

    // A{min,}
    default Regex min(int min) {
        return new RepMin(this, min);
    }

    // A{count}
    default Regex count(int count) {
        return new RepCount(this, count);
    }

    // A^
    default Regex nonGreedy() {
        return new NonGreedy(this);
    }
}
