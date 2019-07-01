package at.searles.parsing.utils.ast.builder;

import at.searles.parsing.Environment;
import at.searles.parsing.Fold;
import at.searles.parsing.ParserStream;
import at.searles.parsing.utils.ast.AstNode;

public class BinaryBuilder<L, V, R> implements Fold<V, R, AstNode> {

    private final L label;
    private final AstNodeBuilder<L> builder;

    public BinaryBuilder(L label, AstNodeBuilder<L> builder) {
        this.label = label;
        this.builder = builder;
    }

    @Override
    public AstNode apply(Environment env, V left, R right, ParserStream stream) {
        return builder.createBin(stream, label, left, right);
    }

    @Override
    public V leftInverse(Environment env, AstNode result) {
        return builder.matchLeft(label);
    }

    @Override
    public R rightInverse(Environment env, AstNode result) {
        return builder.matchRight(label);
    }

    @Override
    public String toString() {
        return "{" + label + "}";
    }
}
