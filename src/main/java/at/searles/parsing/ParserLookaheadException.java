package at.searles.parsing;

public class ParserLookaheadException extends RuntimeException {

    private final Recognizable.Then failedParser;
    private final ParserStream source;

    private final long unitBeforeErrorStart;
    private final long unitBeforeErrorEnd;
    private final long unexpectedTokenStart;
    private final long unexpectedTokenEnd;

    public ParserLookaheadException(Recognizable.Then failedParser, ParserStream stream) {
        this.source = stream;

        this.unitBeforeErrorStart = stream.getStart();
        this.unitBeforeErrorEnd = stream.getEnd();

        this.unexpectedTokenStart = stream.tokStream().frame().startPosition();
        this.unexpectedTokenEnd = stream.tokStream().frame().endPosition();

        this.failedParser = failedParser;
    }

    public Recognizable.Then failedParser() {
        return failedParser;
    }

    public ParserStream source() {
        return source;
    }

    public long getUnitBeforeErrorStart() {
        return unitBeforeErrorStart;
    }

    public long getUnitBeforeErrorEnd() {
        return unitBeforeErrorEnd;
    }

    public long getUnexpectedTokenStart() {
        return unexpectedTokenStart;
    }

    public long getUnexpectedTokenEnd() {
        return unexpectedTokenEnd;
    }

    public String toString() {
        return String.format("%s expected after %s at %d-%d", failedParser.right(), failedParser.left(), getUnexpectedTokenStart(), getUnexpectedTokenEnd());
    }
}
