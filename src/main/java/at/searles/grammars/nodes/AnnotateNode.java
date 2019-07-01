package at.searles.grammars.nodes;

import at.searles.parsing.ParserStream;
import at.searles.parsing.utils.ast.AstNode;

public class AnnotateNode extends AstNode {
    private final ExprNode left;
    private final String annotation;

    public AnnotateNode(ParserStream stream, ExprNode left, String annotation) {
        super(stream);
        this.left = left;
        this.annotation = annotation;
    }
}
