package at.searles.parsing.utils.ast.builder;

import at.searles.parsing.ParserCallBack;
import at.searles.parsing.Mapping;
import at.searles.parsing.ParserStream;
import at.searles.parsing.PrinterCallBack;
import at.searles.parsing.utils.ast.AstNode;
import org.jetbrains.annotations.NotNull;

public class UnaryBuilder<L, V> implements Mapping<V, AstNode> {

    private final L label;
    private final AstNodeBuilder<L> builder;

    public UnaryBuilder(L label, AstNodeBuilder<L> builder) {
        this.label = label;
        this.builder = builder;
    }

    @Override
    public AstNode parse(ParserCallBack env, ParserStream stream, @NotNull V left) {
        return builder.createValue(stream.createSourceInfo(), label, left);
    }

    @Override
    public V left(PrinterCallBack env, @NotNull AstNode result) {
        return builder.matchValue(label, result);
    }

    @Override
    public String toString() {
        return "{" + label + "}";
    }
}
