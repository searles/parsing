package at.searles.lexer.test;

import at.searles.lexer.Lexer;
import at.searles.regexp.CharSet;
import at.searles.regexp.Text;
import org.junit.Assert;
import org.junit.Test;

public class TokenTest {
    @Test
    public void testCorrectHandlingMultipleLexems() {
        Lexer lexer = new Lexer();

        int tok1 = lexer.add(new Text("="));
        int tok2 = lexer.add(new Text("="));

        Assert.assertEquals(tok1, tok2);
    }

    @Test
    public void testHiddenTokens() {
        Lexer lexer = new Lexer();

        int tokIf = lexer.add(new Text("if"));
        int tokId = lexer.add(CharSet.Companion.chars('a', 'z').rep1());
        int tokLe = lexer.add(new Text("=<"));
        int tokEq = lexer.add(new Text("=="));
        int tokGe = lexer.add(new Text(">="));
        int tokAssign = lexer.add(new Text("="));

    }
}
