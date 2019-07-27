package at.searles.regex;

class RepRange implements Regex {

    private final Regex t;
    private final int min;
    private final int max;

    RepRange(Regex t, int min, int max) {
        this.t = t;
        this.min = min;
        this.max = max;
    }

    @Override
    public <A> A accept(Visitor<A> visitor) {
        return visitor.visitRepRange(t, min, max);
    }

    @Override
    public String toString() {
        return String.format("range(%s, %d, %d)", t, min, max);
    }
}
