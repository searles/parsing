package at.searles.parsing;

public class ParserLookaheadException extends RuntimeException {

    private final Recognizable.Then failedParser;
    private final ParserStream source;

    private final long beforeStart;
    private final long beforeEnd;
    private long nextTokenStart;
    private long nextTokenEnd;

    public ParserLookaheadException(Recognizable.Then failedParser, ParserStream stream) {
        this.source = stream;

        this.beforeStart = stream.start();
        this.beforeEnd = stream.end();

        this.nextTokenStart = stream.tokStream().offset();
        this.nextTokenEnd = stream.tokStream().frame().endPosition();

        if(nextTokenStart < beforeEnd) {
            // this would surprise me...
            nextTokenStart = beforeEnd;
        }

        if(nextTokenEnd <= nextTokenStart) {
            nextTokenEnd = nextTokenStart + 1;
        }

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

    public long getNextTokenStart() {
        return nextTokenStart;
    }

    public long getNextTokenEnd() {
        return nextTokenEnd;
    }

    public String toString() {
        return String.format("%s expected after %s at %d-%d", failedParser.right(), failedParser.left(), getNextTokenStart(), getNextTokenEnd());
    }
}
