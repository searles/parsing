package at.searles.parsing.utils.ast.builder;

import at.searles.parsing.ParserCallBack;
import at.searles.parsing.Mapping;
import at.searles.parsing.ParserStream;
import at.searles.parsing.PrinterCallBack;
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
    public AstNode parse(ParserCallBack env, ParserStream stream, @NotNull CharSequence left) {
        return builder.createToken(stream.createSourceInfo(), label, left);
    }

    @Override
    public CharSequence left(PrinterCallBack env, @NotNull AstNode result) {
        return builder.matchToken(label, result);
    }
}
