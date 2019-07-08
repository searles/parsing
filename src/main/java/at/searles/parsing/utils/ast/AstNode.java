package at.searles.parsing.utils.ast;

import at.searles.parsing.ParserStream;

public class AstNode {

    private final long start;
    private final long end;

    protected AstNode(ParserStream stream) {
        this.start = stream.start();
        this.end = stream.end();
    }

    protected AstNode(AstNode predecessor) {
        this.start = predecessor.start;
        this.end = predecessor.end;
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
