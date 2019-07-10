package at.searles.grammars.nodes;

import at.searles.parsing.utils.ast.SourceInfo;

public class FoldNode extends ExprNode {
    private final ExprNode left;
    private final String foldFn;

    public FoldNode(SourceInfo info, ExprNode left, String foldFn) {
        super(info);
        this.left = left;
        this.foldFn = foldFn;
    }
}
