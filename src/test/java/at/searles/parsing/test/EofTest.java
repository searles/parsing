package at.searles.parsing.test;

import at.searles.lexer.Lexer;
import at.searles.lexer.LexerWithHidden;
import at.searles.lexer.Token;
import at.searles.lexer.Tokenizer;
import at.searles.parsing.Environment;
import at.searles.parsing.ParserStream;
import at.searles.parsing.Recognizable;
import at.searles.parsing.Recognizer;
import at.searles.parsing.tokens.TokenRecognizer;
import at.searles.regex.CharSet;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class EofTest {

    private Recognizable parser;
    private TokenRecognizer eof;
    private Environment env;
    private ParserStream stream;

    @Before
    public void setUp() {
        Tokenizer lexer = new Lexer();

        this.parser = Recognizer.fromString("a", lexer, false).rep();

        Token tok = lexer.token(CharSet.chars(-1));
        eof = new TokenRecognizer(tok, false);
        env = (stream, failedParser) -> {
            // ignore
        };
    }

    @Test
    public void testEofWithoutHidden() {
        withInput("aaa");
        actRecognize();

        Assert.assertTrue(eof());
    }

    private boolean eof() {
        return eof.recognize(env, stream);
    }

    private void actRecognize() {
        parser.recognize(env, stream);
    }

    private void withInput(String input) {
        stream = ParserStream.fromString(input);
    }
}
