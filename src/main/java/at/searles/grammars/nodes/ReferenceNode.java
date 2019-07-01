package at.searles.grammars.nodes;

import at.searles.parsing.ParserStream;

public class ReferenceNode extends ExprNode {
    private final String refCode;

    public ReferenceNode(ParserStream stream, String refCode) {
        super(stream);
        this.refCode = refCode;
    }
}
