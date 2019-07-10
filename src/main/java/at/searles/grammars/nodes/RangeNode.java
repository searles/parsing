package at.searles.grammars.nodes;

import at.searles.parsing.ParserStream;
import at.searles.parsing.utils.ast.SourceInfo;

public class RangeNode extends ExprNode {
    private final ExprNode left;
    private final int[] range;

    public RangeNode(SourceInfo info, ExprNode left, int[] range) {
        super(info);
        this.left = left;
        this.range = range;
    }
}
