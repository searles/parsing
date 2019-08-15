package at.searles.parsing;

public class ParserLookaheadException extends RuntimeException {

    private final Recognizable.Then failedParser;
    private final ParserStream stream;

    public ParserLookaheadException(Recognizable.Then failedParser, ParserStream stream) {
        this.stream = stream;
        this.failedParser = failedParser;
    }
}
