package at.searles.parsing;

import at.searles.buf.FrameStream;
import at.searles.lexer.TokStream;
import at.searles.lexer.Token;
import at.searles.parsing.utils.ast.SourceInfo;

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
    private Listener listener;

    public ParserStream(TokStream stream) {
        this.stream = stream;
        this.parsedStart = this.parsedEnd = stream.offset();
    }
    
    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public static ParserStream fromString(String string) {
        return new ParserStream(TokStream.fromString(string));
    }

    public long start() {
        return this.parsedStart;
    }

    public long end() {
        return this.parsedEnd;
    }

    public SourceInfo createSourceInfo() {
        return new RangeSourceInfo(this);
    }

    public void setStart(long start) {
        this.parsedStart = start;
    }

    public void setEnd(long end) {
        this.parsedEnd = end;
    }

    public CharSequence parseToken(Token token, boolean exclusive) {
        FrameStream.Frame frame = token.parseToken(stream, exclusive);

        if (frame == null) {
            return null;
        }

        setStart(frame.startPosition());
        setEnd(frame.endPosition());

        return frame;
    }

    /**
     * Sets the underlying stream to the given offset, ie,
     * the next token matched will start at the given parameter.
     * The caller must take care of start and end of the parsed
     * unit.
     *
     * @param offset The new offset.
     */
    public void setOffset(long offset) {
        stream.setPositionTo(offset);
    }

    /**
     * Returns the position from which the next token will be consumed
     */
    public long offset() {
        return stream.offset();
    }

    public String toString() {
        return stream.toString() + ": [" + parsedStart + ", " + parsedEnd + "]";
    }

    public <C> void notifyAnnotationBegin(C annotation) {
        if(listener != null) {
            listener.annotationBegin(annotation);
        }
    }

    /**
     * Override this method if necessary
     */
    public <C> void notifyAnnotationEnd(C annotation, boolean success) {
        if(listener != null) {
            listener.annotationEnd(annotation, success);
        }
    }

    private static class RangeSourceInfo implements SourceInfo {

        private final long end;
        private final long start;

        RangeSourceInfo(ParserStream stream) {
            this.start = stream.parsedStart;
            this.end = stream.parsedEnd;
        }

        @Override
        public long start() {
            return start;
        }

        @Override
        public long end() {
            return end;
        }

        public String toString() {
            return String.format("[%d:%d]", start(), end());
        }
    }

    public interface Listener {
        /**
         * If an annotation starts, the parser after it not necessarily succeeds
         * even in an LL1-Grammar. Yet, all calls to this method are
         * followed by a call to annotationEnd. Hence, all changes done
         * in this method must be undone if the arguments to annotationEnd
         * indicate that the annotation parser did not succeed.
         */
        <C> void annotationBegin(C annotation);

        <C> void annotationEnd(C annotation, boolean success);
    }
}
