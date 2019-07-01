package at.searles.buf;

public interface CharStream {
    /**
     * Returns the next char from this stream (unicode).
     * This method considers chars beyond the 65536-limit in
     * unicode.
     * @return -1 if there are no more elements.
     */
    int next();
}
