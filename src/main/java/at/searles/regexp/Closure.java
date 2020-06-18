package at.searles.regexp;

class Closure implements Regexp {

    private final Regexp t;
    private final boolean reflexive;
    private final boolean transitive;

    Closure(Regexp t, boolean reflexive, boolean transitive) {
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
