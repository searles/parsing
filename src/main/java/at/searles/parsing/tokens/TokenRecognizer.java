package at.searles.parsing.tokens;

import at.searles.lexer.Token;
import at.searles.lexer.Tokenizer;
import at.searles.parsing.Environment;
import at.searles.parsing.ParserStream;
import at.searles.parsing.Recognizer;
import at.searles.parsing.printing.ConcreteSyntaxTree;
import org.jetbrains.annotations.NotNull;

public class TokenRecognizer implements Recognizer {

    private final String str;
    private final Token token;
    private final boolean exclusive;

    /**
     * @param str This string is also needed for inversion
     */
    public TokenRecognizer(String str, Tokenizer lexer, boolean exclusive) {
        this.str = str;
        this.token = lexer.token(str);
        this.exclusive = exclusive;
    }

    public TokenRecognizer(Token token, boolean exclusive) {
        this.str = token.toString();
        this.token = token;
        this.exclusive = exclusive;
    }

    @Override
    public boolean recognize(Environment env, ParserStream stream) {
        return stream.parseToken(token, exclusive) != null;
    }

    @NotNull
    @Override
    public ConcreteSyntaxTree print(Environment env) {
        return ConcreteSyntaxTree.fromCharSequence(str);
    }

    public String toString() {
        return str;
    }
}