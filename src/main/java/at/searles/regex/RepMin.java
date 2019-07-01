package at.searles.regex;

class RepMin implements Regex {

    private final Regex regex;
    private final int min;

    RepMin(Regex regex, int min) {
        this.regex = regex;
        this.min = min;
    }

    @Override
    public <A> A accept(Visitor<A> visitor) {
        return visitor.visitRepMin(regex, min);
    }

    @Override
    public String toString() {
        return String.format("min(%s, %d)", regex, min);
    }
}
