package at.searles.parsing.utils.ast;

import at.searles.parsing.ParserStream;

public class AstNode {

    private final long start;
    private final long end;

    protected AstNode(ParserStream stream) {
        this.start = stream.start();
        this.end = stream.end();
    }

    public long start() {
        return start;
    }

    public long end() {
        return end;
    }

    public String toString() {
        return String.format("[%d:%d]", start, end);
    }
}
