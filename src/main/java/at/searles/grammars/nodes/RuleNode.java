package at.searles.grammars.nodes;

import at.searles.parsing.ParserStream;
import at.searles.parsing.utils.ast.AstNode;

public class RuleNode extends AstNode {
    private final String id;
    private final ExprNode rhs;

    public RuleNode(ParserStream stream, String id, ExprNode rhs) {
        super(stream);
        this.id = id;
        this.rhs = rhs;
    }

    boolean isRegex() {
        return id.toUpperCase().equals(id);
    }
}
