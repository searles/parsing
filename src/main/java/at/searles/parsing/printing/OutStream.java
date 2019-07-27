package at.searles.parsing.printing;

public interface OutStream {
    void append(CharSequence seq);

    void append(int codePoint);
}
