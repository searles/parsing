package at.searles.parsing.tokens;

import at.searles.lexer.Token;
import at.searles.parsing.*;
import at.searles.parsing.printing.ConcreteSyntaxTree;

public class TokenParser<A> implements Parser<A> {

    private final Token token;
    private final Mapping<CharSequence, A> mapping;
    private final boolean exclusive;

    public TokenParser(Token token, Mapping<CharSequence, A> mapping, boolean exclusive) {
        this.token = token;
        this.mapping = mapping;
        this.exclusive = exclusive;
    }

    @Override
    public boolean recognize(ParserStream stream) {
        return stream.parseToken(token, exclusive) != null;
    }

    @Override
    public A parse(ParserStream stream) {
        CharSequence seq = stream.parseToken(token, exclusive);

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
        return token.toString();
    }
}
