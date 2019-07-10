package at.searles.parsing.utils.ast.builder;

import at.searles.parsing.Environment;
import at.searles.parsing.Initializer;
import at.searles.parsing.ParserStream;
import at.searles.parsing.utils.ast.AstNode;

public class EmptyBuilder<L> implements Initializer<AstNode> {
    private final L label;
    private final AstNodeBuilder<L> builder;

    public EmptyBuilder(L label, AstNodeBuilder<L> builder) {
        this.label = label;
        this.builder = builder;
    }

    @Override
    public AstNode parse(Environment env, ParserStream stream) {
        return builder.createItem(stream.createSourceInfo(), label);
    }

    @Override
    public boolean consume(Environment env, AstNode node) {
        return builder.matchItem(label);
    }
}
