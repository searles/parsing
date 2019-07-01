package at.searles.regex;

class Concat implements Regex {

    private final Regex l;
    private final Regex r;

    Concat(Regex l, Regex r) {
        this.l = l;
        this.r = r;
    }

    public Regex l() {
        return l;
    }

    public Regex r() {
        return r;
    }

    @Override
    public <A> A accept(Visitor<A> visitor) {
        return visitor.visitThen(l, r);
    }

    @Override
    public String toString() {
        return String.format("then(%s, %s)", l, r);
    }
}
