package at.searles.buf;

import java.io.IOException;
import java.io.Reader;

/**
 * This class creates a CharStream out of a Reader.
 */
public class ReaderCharStream implements CharStream {
    private final Reader r;
    private long offset;

    public ReaderCharStream(Reader r) {
        this.r = r;
        this.offset = 0;
    }

    @Override
    public int next() {
        try {
            int ch = r.read();

            if(ch == -1) {
                return -1;
            }

            offset ++;

            if(Character.isHighSurrogate((char) ch)) {
                int low = r.read();

                if(low == -1) {
                    throw new IllegalArgumentException("no lo surrogate from this reader");
                }

                offset ++;

                return Character.toCodePoint((char) ch, (char) low);
            }

            return ch; // char = codepoint.
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public String toString() {
        return r.toString() + "@" + offset;
    }
}
