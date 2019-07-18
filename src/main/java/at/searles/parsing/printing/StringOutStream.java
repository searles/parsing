package at.searles.parsing.printing;

public class StringOutStream implements OutStream {

    private StringBuilder sb = new StringBuilder();

    @Override
    public void append(CharSequence seq) {
        sb.append(seq);
    }

    @Override
    public void append(int codePoint) {
        sb.appendCodePoint(codePoint);
    }

    @Override
    public String toString() {
        return sb.toString();
    }
}
