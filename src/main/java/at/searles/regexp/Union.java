package at.searles.regexp;

class Union implements Regexp {

    private final Regexp l;
    private final Regexp r;

    Union(Regexp l, Regexp r) {
        this.l = l;
        this.r = r;
    }

    public String toString() {
        return "or(" + l.toString() + ", " + r.toString() + ")";
    }

    @Override
    public <A> A accept(Visitor<A> visitor) {
        return visitor.visitOr(l, r);
    }
}
