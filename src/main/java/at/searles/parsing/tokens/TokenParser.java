package at.searles.parsing.tokens;

import at.searles.lexer.Tokenizer;
import at.searles.parsing.*;
import at.searles.parsing.printing.ConcreteSyntaxTree;

public class TokenParser<A> implements Parser<A> {

    private final int tokenId;
    private final Tokenizer tokenizer;
    private final Mapping<CharSequence, A> mapping;
    private final boolean exclusive;

    public TokenParser(int tokenId, Tokenizer tokenizer, boolean exclusive, Mapping<CharSequence, A> mapping) {
        this.tokenId = tokenId;
        this.tokenizer = tokenizer;
        this.mapping = mapping;
        this.exclusive = exclusive;
    }

    @Override
    public boolean recognize(ParserStream stream) {
        return stream.parseToken(tokenizer, tokenId, exclusive) != null;
    }

    @Override
    public A parse(ParserStream stream) {
        CharSequence seq = stream.parseToken(tokenizer, tokenId, exclusive);

        if (seq == null) {
            return null;
        }

        return mapping.parse(stream, seq);
    }

    @Override
    public ConcreteSyntaxTree print(A a) {
        CharSequence seq = mapping.left(a);

        if (seq == null) {
            return null;
        }

        return ConcreteSyntaxTree.fromCharSequence(seq);
    }

    public String toString() {
        return String.format("<%d>", tokenId);
    }
}
