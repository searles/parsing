package at.searles.grammars.nodes;

import at.searles.parsing.ParserStream;

public class RangeNode extends ExprNode {
    private final ExprNode left;
    private final int[] range;

    public RangeNode(ParserStream stream, ExprNode left, int[] range) {
        super(stream);
        this.left = left;
        this.range = range;
    }
}
