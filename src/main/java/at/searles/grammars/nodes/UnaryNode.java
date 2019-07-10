package at.searles.grammars.nodes;

import at.searles.grammars.ParserGenerator;
import at.searles.parsing.ParserStream;
import at.searles.parsing.utils.ast.SourceInfo;

public class UnaryNode extends ExprNode {
    private final ParserGenerator.Type op;
    private final ExprNode arg;

    public UnaryNode(SourceInfo info, ParserGenerator.Type op, ExprNode arg) {
        super(info);
        this.op = op;
        this.arg = arg;
    }
}
