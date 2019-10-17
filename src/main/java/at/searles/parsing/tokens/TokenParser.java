package at.searles.parsing.tokens;

import at.searles.lexer.Tokenizer;
import at.searles.parsing.*;
import at.searles.parsing.printing.ConcreteSyntaxTree;

public class TokenParser implements Parser<CharSequence> {

    private final int tokenId;
    private final Tokenizer tokenizer;
    private final boolean exclusive;

    public TokenParser(int tokenId, Tokenizer tokenizer, boolean exclusive) {
        this.tokenId = tokenId;
        this.tokenizer = tokenizer;
        this.exclusive = exclusive;
    }

    @Override
    public boolean recognize(ParserStream stream) {
        return stream.parseToken(tokenizer, tokenId, exclusive) != null;
    }

    @Override
    public CharSequence parse(ParserStream stream) {
        return stream.parseToken(tokenizer, tokenId, exclusive);
    }

    @Override
    public ConcreteSyntaxTree print(CharSequence seq) {
        return ConcreteSyntaxTree.fromCharSequence(seq);
    }

    public String toString() {
        return String.format("<%d>", tokenId);
    }
}
