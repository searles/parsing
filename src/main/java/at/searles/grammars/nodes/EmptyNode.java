package at.searles.grammars.nodes;

import at.searles.parsing.ParserStream;
import at.searles.parsing.utils.ast.AstNode;

public class EmptyNode extends ExprNode {
    public EmptyNode(ParserStream stream) {
        super(stream);
    }
}
