package at.searles.regexp;

class RepRange implements Regexp {

    private final Regexp t;
    private final int min;
    private final int max;

    RepRange(Regexp t, int min, int max) {
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
