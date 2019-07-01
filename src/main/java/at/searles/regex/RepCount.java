package at.searles.regex;

class RepCount implements Regex {
    private final Regex regex;
    private final int count;

    RepCount(Regex regex, int count) {
        this.regex = regex;
        this.count = count;
    }

    @Override
    public <A> A accept(Visitor<A> visitor) {
        return visitor.visitRepCount(regex, count);
    }

    @Override
    public String toString() {
        return String.format("count(%s, %d)", regex, count);
    }
}
