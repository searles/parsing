package at.searles.regexp;

class RepMin implements Regexp {

    private final Regexp regexp;
    private final int min;

    RepMin(Regexp regexp, int min) {
        this.regexp = regexp;
        this.min = min;
    }

    @Override
    public <A> A accept(Visitor<A> visitor) {
        return visitor.visitRepMin(regexp, min);
    }

    @Override
    public String toString() {
        return String.format("min(%s, %d)", regexp, min);
    }
}
