package at.searles.parsing;

import at.searles.lexer.TokStream;
import at.searles.lexer.Token;

public class ParserStream {
    private final TokStream stream;

    /**
     * Marks the start of the current parsed element.
     */
    private long parsedStart;

    /**
     * Marks the end of the current parsed element.
     */
    private long parsedEnd;

    public static ParserStream fromString(String string) {
        return new ParserStream(TokStream.fromString(string));
    }

    public ParserStream(TokStream stream) {
        this.stream = stream;
        this.parsedStart = this.parsedEnd = stream.offset();
    }


    public long start() {
        return this.parsedStart;
    }

    public long end() {
        return this.parsedEnd;
    }

    public void setStart(long start) {
        this.parsedStart = start;
    }

    public void setEnd(long end) {
        this.parsedEnd = end;
    }

    public CharSequence parseToken(Token token, boolean exclusive) {
        CharSequence seq = token.parseToken(stream, exclusive);

        if(seq == null) {
            return null;
        }

        setStart(stream.frameStart());
        setEnd(stream.frameEnd());

        return seq;
    }

    /**
     * Sets the underlying stream to the given offset, ie,
     * the next token matched will start at the given parameter.
     * The caller must take care of start and end of the parsed
     * unit.
     * @param offset The new offset.
     */
    public void setOffset(long offset) {
        stream.setPtr(offset);
    }

    /**
     * Returns the ptr from which the next token will be consumed
     */
    public long offset() {
        return stream.offset();
    }

    public String toString() {
        return stream.toString() + ": [" + parsedStart + ", " + parsedEnd + "]";
    }
}
