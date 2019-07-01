package at.searles.regex;

class Closure implements Regex {

    private final Regex t;
    private final boolean reflexive;
    private final boolean transitive;

    Closure(Regex t, boolean reflexive, boolean transitive) {
        this.t = t;
        this.reflexive = reflexive;
        this.transitive = transitive;
    }

    @Override
    public <A> A accept(Visitor<A> visitor) {
        return visitor.visitClosure(t, reflexive, transitive);
    }

    @Override
    public String toString() {
        return String.format("closure(%s, %b, %b)", t, reflexive, transitive);
    }
}
