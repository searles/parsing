package at.searles.regexparser;

public class CodePointStream {
    private final String string;
    private int i = 0;

    CodePointStream(String string) {
        this.string = string;
    }

    public int get() {
        return i < string.length() ? string.codePointAt(i) : -1;
    }

    public CodePointStream incr() {
        i += Character.charCount(get());
        return this;
    }

    public boolean end() {
        return i >= string.length();
    }

    public CodePointStream trim() {
        while(!end() && Character.isWhitespace(get())) {
            incr();
        }

        return this;
    }

    @Override
    public String toString() {
        return string.substring(0, i) + "_" + string.substring(i);
    }
}
