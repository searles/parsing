package at.searles.regex;

/**
 * Strictly speaking not a regex, but a
 * good and efficient replacement for non-greedy.
 */
class NonGreedy implements Regex {

    private final Regex t;

    NonGreedy(Regex t) {
        this.t = t;
    }

    @Override
    public <A> A accept(Visitor<A> visitor) {
        return visitor.visitNonGreedy(t);
    }

    @Override
    public String toString() {
        return String.format("nonGreedy(%s)", t);
    }
}
