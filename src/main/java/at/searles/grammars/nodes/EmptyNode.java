package at.searles.grammars.nodes;

import at.searles.parsing.ParserStream;
import at.searles.parsing.utils.ast.AstNode;
import at.searles.parsing.utils.ast.SourceInfo;

public class EmptyNode extends ExprNode {
    public EmptyNode(SourceInfo info) {
        super(info);
    }
}
