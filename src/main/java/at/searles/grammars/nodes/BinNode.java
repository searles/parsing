package at.searles.grammars.nodes;

import at.searles.grammars.ParserGenerator;
import at.searles.parsing.utils.ast.SourceInfo;

public class BinNode extends ExprNode {
    private final ParserGenerator.Type op;
    private final ExprNode left;
    private final ExprNode right;

    public BinNode(SourceInfo info, ParserGenerator.Type op, ExprNode left, ExprNode right) {
        super(info);
        this.op = op;
        this.left = left;
        this.right = right;
    }
}
