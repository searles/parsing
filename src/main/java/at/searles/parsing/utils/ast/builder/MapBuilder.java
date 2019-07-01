package at.searles.parsing.utils.ast.builder;

import at.searles.parsing.Environment;
import at.searles.parsing.Mapping;
import at.searles.parsing.ParserStream;
import at.searles.parsing.utils.ast.AstNode;

import java.util.Map;

public class MapBuilder<L, V> implements Mapping<Map<L, V>, AstNode> {
    private final L label;
    private final AstNodeBuilder<L> builder;

    public MapBuilder(L label, AstNodeBuilder<L> builder) {
        this.label = label;
        this.builder = builder;
    }

    @Override
    public AstNode parse(Environment env, Map<L, V> left, ParserStream stream) {
        return builder.createMap(stream, label, left);
    }

    @Override
    public Map<L, V> left(Environment env, AstNode result) {
        return builder.matchMap(label, result);
    }

    @Override
    public String toString() {
        return "{node}";
    }
}
