package at.searles.parsing.tokens;

import at.searles.lexer.Tokenizer;
import at.searles.parsing.ParserStream;
import at.searles.parsing.Recognizer;
import at.searles.parsing.printing.ConcreteSyntaxTree;
import org.jetbrains.annotations.NotNull;

public class TokenRecognizer implements Recognizer {

    private final boolean exclusive;
    private final String printed;
    private final int tokId;
    private final Tokenizer tokenizer;

    public TokenRecognizer(int tokId, Tokenizer tokenizer, boolean exclusive, String printed) {
        this.tokId = tokId;
        this.tokenizer = tokenizer;
        this.exclusive = exclusive;
        this.printed = printed;
    }

    @Override
    public boolean recognize(ParserStream stream) {
        return stream.parseToken(tokenizer, tokId, exclusive) != null;
    }

    @NotNull
    @Override
    public ConcreteSyntaxTree print() {
        return ConcreteSyntaxTree.fromCharSequence(printed);
    }

    public String toString() {
        return printed;
    }
}