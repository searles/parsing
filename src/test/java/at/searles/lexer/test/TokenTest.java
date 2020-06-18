package at.searles.lexer.test;

import at.searles.lexer.Lexer;
import at.searles.regexp.CharSet;
import at.searles.regexp.Regexp;
import org.junit.Assert;
import org.junit.Test;

public class TokenTest {
    @Test
    public void testCorrectHandlingMultipleLexems() {
        Lexer lexer = new Lexer();

        int tok1 = lexer.add(Regexp.text("="));
        int tok2 = lexer.add(Regexp.text("="));

        Assert.assertEquals(tok1, tok2);
    }

    @Test
    public void testHiddenTokens() {
        Lexer lexer = new Lexer();

        int tokIf = lexer.add(Regexp.text("if"));
        int tokId = lexer.add(CharSet.chars('a', 'z').plus());
        int tokLe = lexer.add(Regexp.text("=<"));
        int tokEq = lexer.add(Regexp.text("=="));
        int tokGe = lexer.add(Regexp.text(">="));
        int tokAssign = lexer.add(Regexp.text("="));

    }
}
