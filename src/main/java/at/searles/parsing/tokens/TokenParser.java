package at.searles.parsing.tokens;

import at.searles.lexer.Token;
import at.searles.parsing.Environment;
import at.searles.parsing.Mapping;
import at.searles.parsing.Parser;
import at.searles.parsing.ParserStream;
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
    public boolean recognize(Environment env, ParserStream stream) {
        return stream.parseToken(token, exclusive) != null;
    }

    @Override
    public A parse(Environment env, ParserStream stream) {
        CharSequence seq = stream.parseToken(token, exclusive);

        if(seq == null) {
            return null;
        }

        return mapping.parse(env, stream, seq);
    }

    @Override
    public ConcreteSyntaxTree print(Environment env, A a) {
        CharSequence seq = mapping.left(env, a);

        if(seq == null) {
            return null;
        }

        return ConcreteSyntaxTree.fromCharSequence(seq);
    }

    public String toString() {
        return token.toString();
    }
}
