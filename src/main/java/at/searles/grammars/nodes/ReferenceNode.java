package at.searles.grammars.nodes;

import at.searles.parsing.utils.ast.SourceInfo;

public class ReferenceNode extends ExprNode {
    private final String refCode;

    public ReferenceNode(SourceInfo info, String refCode) {
        super(info);
        this.refCode = refCode;
    }
}
