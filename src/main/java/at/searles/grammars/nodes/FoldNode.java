package at.searles.grammars.nodes;

import at.searles.parsing.ParserStream;

public class FoldNode extends ExprNode {
    private final ExprNode left;
    private final String foldFn;

    public FoldNode(ParserStream stream, ExprNode left, String foldFn) {
        super(stream);
        this.left = left;
        this.foldFn = foldFn;
    }
}
