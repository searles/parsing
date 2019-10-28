package at.searles.parsing;

public class ParserLookaheadException extends RuntimeException {

    private final Recognizable.Then failedParser;
    private final ParserStream source;

    private final long beforeStart;
    private final long beforeEnd;
    private final long failedTokenStart;
    private final long failedTokenEnd;

    public ParserLookaheadException(Recognizable.Then failedParser, ParserStream stream) {
        this.source = stream;

        this.beforeStart = stream.getStart();
        this.beforeEnd = stream.getEnd();

        this.failedTokenStart = stream.tokStream().frame().startPosition();
        this.failedTokenEnd = stream.tokStream().frame().endPosition();

        this.failedParser = failedParser;
    }

    public Recognizable.Then failedParser() {
        return failedParser;
    }

    public ParserStream source() {
        return source;
    }

    public long getBeforeStart() {
        return beforeStart;
    }

    public long getBeforeEnd() {
        return beforeEnd;
    }

    public long getFailedTokenStart() {
        return failedTokenStart;
    }

    public long getFailedTokenEnd() {
        return failedTokenEnd;
    }

    public String toString() {
        return String.format("%s expected after %s at %d-%d", failedParser.right(), failedParser.left(), getFailedTokenStart(), getFailedTokenEnd());
    }
}
