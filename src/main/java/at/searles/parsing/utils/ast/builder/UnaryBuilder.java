package at.searles.parsing.utils.ast.builder;

import at.searles.parsing.Environment;
import at.searles.parsing.Mapping;
import at.searles.parsing.ParserStream;
import at.searles.parsing.utils.ast.AstNode;

public class UnaryBuilder<L, V> implements Mapping<V, AstNode> {

    private final L label;
    private final AstNodeBuilder<L> builder;

    public UnaryBuilder(L label, AstNodeBuilder<L> builder) {
        this.label = label;
        this.builder = builder;
    }

    @Override
    public AstNode parse(Environment env, V left, ParserStream stream) {
        return builder.createValue(stream, label, left);
    }

    @Override
    public V left(Environment env, AstNode result) {
        return builder.matchValue(label, result);
    }

    @Override
    public String toString() {
        return "{" + label + "}";
    }
}
