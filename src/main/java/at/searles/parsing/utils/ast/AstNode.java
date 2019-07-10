package at.searles.parsing.utils.ast;

import at.searles.parsing.ParserStream;

public class AstNode {

    private final SourceInfo sourceInfo;

    protected AstNode(SourceInfo sourceInfo) {
        this.sourceInfo = sourceInfo;
    }

    public SourceInfo sourceInfo() {
        return sourceInfo;
    }

    public String toString() {
        return String.format("[%d:%d]", sourceInfo.start(), sourceInfo.end());
    }
}
