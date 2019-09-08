package at.searles.parsing;

public class ParserLookaheadException extends RuntimeException {

    private final Recognizable.Then failedParser;
    private final ParserStream source;
    private final long start;
    private final long end;

    public ParserLookaheadException(Recognizable.Then failedParser, ParserStream stream) {
        this.source = stream;
        this.start = stream.start();
        this.end = stream.end();

        this.failedParser = failedParser;
    }

    // TODO unit test!

    /**
     * @return The position of the successful match before the mismatch
     */
    public long start() {
        return start;
    }

    /**
     * @return The end position of the successful match before the mismatch
     */
    public long end() {
        return end;
    }

    public Recognizable.Then failedParser() {
        return failedParser;
    }

    public ParserStream source() {
        return source;
    }
}
