package at.searles.regexp;

class Concat implements Regexp {

    private final Regexp l;
    private final Regexp r;

    Concat(Regexp l, Regexp r) {
        this.l = l;
        this.r = r;
    }

    public Regexp l() {
        return l;
    }

    public Regexp r() {
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
