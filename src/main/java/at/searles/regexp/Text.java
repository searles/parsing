package at.searles.regexp;


class Text implements Regexp {

    private final String string;

    Text(CharSequence seq) {
        this.string = seq.toString();
    }

    @Override
    public String toString() {
        return string;
    }

    @Override
    public <A> A accept(Visitor<A> visitor) {
        return visitor.visitText(string);
    }
}
