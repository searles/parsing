package at.searles.parsing.utils.ast.builder;

import at.searles.parsing.Environment;
import at.searles.parsing.Mapping;
import at.searles.parsing.ParserStream;
import at.searles.parsing.utils.ast.AstNode;
import org.jetbrains.annotations.NotNull;

public class TokenBuilder<L> implements Mapping<CharSequence, AstNode> {

    private final L label;
    private final AstNodeBuilder<L> builder;

    public TokenBuilder(L label, AstNodeBuilder<L> builder) {
        this.label = label;
        this.builder = builder;
    }

    @Override
    public AstNode parse(Environment env, @NotNull CharSequence left, ParserStream stream) {
        return builder.createToken(stream.createSourceInfo(), label, left);
    }

    @Override
    public CharSequence left(Environment env, AstNode result) {
        return builder.matchToken(label, result);
    }
}
