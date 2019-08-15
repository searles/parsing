package at.searles.parsing.utils.ast.builder;

import at.searles.parsing.Fold;
import at.searles.parsing.ParserStream;
import at.searles.parsing.utils.ast.AstNode;
import org.jetbrains.annotations.NotNull;

public class BinaryBuilder<L, V, R> implements Fold<V, R, AstNode> {

    private final L label;
    private final AstNodeBuilder<L> builder;

    public BinaryBuilder(L label, AstNodeBuilder<L> builder) {
        this.label = label;
        this.builder = builder;
    }

    @Override
    public AstNode apply(ParserStream stream, @NotNull V left, @NotNull R right) {
        return builder.createBin(stream.createSourceInfo(), label, left, right);
    }

    @Override
    public V leftInverse(@NotNull AstNode result) {
        return builder.matchLeft(label);
    }

    @Override
    public R rightInverse(@NotNull AstNode result) {
        return builder.matchRight(label);
    }

    @Override
    public String toString() {
        return "{" + label + "}";
    }
}
