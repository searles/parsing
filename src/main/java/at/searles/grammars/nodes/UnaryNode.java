package at.searles.grammars.nodes;

import at.searles.grammars.ParserGenerator;
import at.searles.parsing.ParserStream;

public class UnaryNode extends ExprNode {
    private final ParserGenerator.Type op;
    private final ExprNode arg;

    public UnaryNode(ParserStream stream, ParserGenerator.Type op, ExprNode arg) {
        super(stream);
        this.op = op;
        this.arg = arg;
    }
}
