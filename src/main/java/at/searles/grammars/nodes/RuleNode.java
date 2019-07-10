package at.searles.grammars.nodes;

import at.searles.parsing.ParserStream;
import at.searles.parsing.utils.ast.AstNode;
import at.searles.parsing.utils.ast.SourceInfo;

public class RuleNode extends AstNode {
    private final String id;
    private final ExprNode rhs;

    public RuleNode(SourceInfo info, String id, ExprNode rhs) {
        super(info);
        this.id = id;
        this.rhs = rhs;
    }

    boolean isRegex() {
        return id.toUpperCase().equals(id);
    }
}
