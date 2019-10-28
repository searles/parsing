package at.searles.parsing;

import at.searles.buf.Frame;
import at.searles.lexer.TokenStream;
import at.searles.lexer.Tokenizer;
import at.searles.parsing.utils.ast.SourceInfo;

public class ParserStream {
    private final TokenStream stream;

    /**
     * Marks the start of the current parsed element.
     */
    private long parsedStart;

    /**
     * Marks the end of the current parsed element.
     */
    private long parsedEnd;
    private Listener listener;

    public ParserStream(TokenStream stream) {
        this.stream = stream;
        this.parsedStart = this.parsedEnd = stream.offset();
    }

    public TokenStream tokStream() {
        return stream;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public static ParserStream fromString(String string) {
        return new ParserStream(TokenStream.fromString(string));
    }

    public long getStart() {
        return this.parsedStart;
    }

    public long getEnd() {
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

    public CharSequence parseToken(Tokenizer tokenizer, int tokId, boolean exclusive) {
        Frame frame = tokenizer.matchToken(stream, tokId, exclusive);

        if(frame != null) {
            setStart(frame.startPosition());
            setEnd(frame.endPosition());
        }

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
    public void backtrackToOffset(long offset) {
        stream.setPositionTo(offset);
    }

    /**
     * Returns the position from which the next token will be consumed
     */
    public long getOffset() {
        return stream.offset();
    }

    public String toString() {
        return stream.toString() + ": [" + parsedStart + ", " + parsedEnd + "]";
    }

    public <C> void notifyAnnotationBegin(C annotation) {
        if(listener != null) {
            listener.annotationBegin(this, annotation);
        }
    }

    /**
     * Override this method if necessary
     */
    public <C> void notifyAnnotationEnd(C annotation, boolean success) {
        if(listener != null) {
            listener.annotationEnd(this, annotation, success);
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
        <C> void annotationBegin(ParserStream parserStream, C annotation);

        <C> void annotationEnd(ParserStream parserStream, C annotation, boolean success);
    }

    public interface SimpleListener extends Listener {
        @Override
        default <C> void annotationBegin(ParserStream parserStream, C annotation) {}

        @Override
        default <C> void annotationEnd(ParserStream parserStream, C annotation, boolean success) {
            if(success) {
                annotate(parserStream, annotation);
            }
        }

        <C> void annotate(ParserStream parserStream, C annotation);
    }
}
