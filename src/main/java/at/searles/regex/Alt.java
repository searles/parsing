package at.searles.regex;

class Alt implements Regex {

    private final Regex l;
    private final Regex r;

    Alt(Regex l, Regex r) {
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
