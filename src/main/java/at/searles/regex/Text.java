package at.searles.regex;


class Text implements Regex {

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
