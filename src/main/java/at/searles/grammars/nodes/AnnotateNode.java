package at.searles.grammars.nodes;

import at.searles.parsing.ParserStream;
import at.searles.parsing.utils.ast.AstNode;
import at.searles.parsing.utils.ast.SourceInfo;

public class AnnotateNode extends AstNode {
    private final ExprNode left;
    private final String annotation;

    public AnnotateNode(SourceInfo info, ExprNode left, String annotation) {
        super(info);
        this.left = left;
        this.annotation = annotation;
    }
}
