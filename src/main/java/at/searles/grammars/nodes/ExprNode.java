package at.searles.grammars.nodes;

import at.searles.parsing.ParserStream;
import at.searles.parsing.utils.ast.AstNode;

public class ExprNode extends AstNode {
    protected ExprNode(ParserStream stream) {
        super(stream);
    }
}
