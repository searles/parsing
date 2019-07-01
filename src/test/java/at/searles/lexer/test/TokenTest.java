package at.searles.lexer.test;

import at.searles.lexer.Lexer;
import at.searles.regex.CharSet;
import at.searles.regex.Regex;
import org.junit.Assert;
import org.junit.Test;

public class TokenTest {
    @Test
    public void testCorrectHandlingMultipleLexems() {
        Lexer lexer = new Lexer();

        int tok1 = lexer.add(Regex.text("="));
        int tok2 = lexer.add(Regex.text("="));

        Assert.assertEquals(tok1, tok2);
    }

    @Test
    public void testHiddenTokens() {
        Lexer lexer = new Lexer();

        int tokIf = lexer.add(Regex.text("if"));
        int tokId = lexer.add(CharSet.chars('a', 'z').plus());
        int tokLe = lexer.add(Regex.text("=<"));
        int tokEq = lexer.add(Regex.text("=="));
        int tokGe = lexer.add(Regex.text(">="));
        int tokAssign = lexer.add(Regex.text("="));

    }
}
