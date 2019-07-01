package at.searles.grammars.nodes;

import at.searles.grammars.ParserGenerator;
import at.searles.parsing.ParserStream;

public class BinNode extends ExprNode {
    private final ParserGenerator.Type op;
    private final ExprNode left;
    private final ExprNode right;

    public BinNode(ParserStream stream, ParserGenerator.Type op, ExprNode left, ExprNode right) {
        super(stream);
        this.op = op;
        this.left = left;
        this.right = right;
    }
}
