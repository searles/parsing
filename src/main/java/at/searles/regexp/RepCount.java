package at.searles.regexp;

class RepCount implements Regexp {
    private final Regexp regexp;
    private final int count;

    RepCount(Regexp regexp, int count) {
        this.regexp = regexp;
        this.count = count;
    }

    @Override
    public <A> A accept(Visitor<A> visitor) {
        return visitor.visitRepCount(regexp, count);
    }

    @Override
    public String toString() {
        return String.format("count(%s, %d)", regexp, count);
    }
}
