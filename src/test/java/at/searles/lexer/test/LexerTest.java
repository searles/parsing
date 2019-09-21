package at.searles.lexer.test;

import at.searles.lexer.Lexer;
import at.searles.lexer.TokenStream;
import at.searles.lexer.utils.IntSet;
import at.searles.regex.CharSet;
import at.searles.regex.Regex;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test for basic lexer functionality.
 */
public class LexerTest {

    private Lexer lexer;
    private int token;

    private void with(Regex regex) {
        lexer = new Lexer();
        token = lexer.add(regex);
    }

    private void testIfIsAccepted(String string, String expected) {
        TokenStream stream = TokenStream.fromString(string);

        IntSet tokIds = stream.current(lexer);

        Assert.assertEquals(expected != null, tokIds != null);

        Assert.assertEquals(expected != null, tokIds != null && tokIds.contains(token));

        Assert.assertEquals(expected, tokIds != null ? stream.frame().toString() : null);
    }

    @Test
    public void testRange02() {
        with(CharSet.chars('a').range(0, 2));
        testIfIsAccepted("b", "");
        testIfIsAccepted("a", "a");
        testIfIsAccepted("aa", "aa");
        testIfIsAccepted("aaa", "aa");
    }

    @Test
    public void testRange13() {
        with(CharSet.chars('a').range(1, 3));
        testIfIsAccepted("b", null);
        testIfIsAccepted("a", "a");
        testIfIsAccepted("aa", "aa");
        testIfIsAccepted("aaa", "aaa");
        testIfIsAccepted("aaaa", "aaa");
    }

    @Test
    public void testRange23() {
        with(CharSet.chars('a').range(2, 3));
        testIfIsAccepted("b", null);
        testIfIsAccepted("a", null);
        testIfIsAccepted("aa", "aa");
        testIfIsAccepted("aaa", "aaa");
        testIfIsAccepted("aaaa", "aaa");
    }

    @Test
    public void testRange24() {
        with(CharSet.chars('a').range(2, 4));
        testIfIsAccepted("b", null);
        testIfIsAccepted("a", null);
        testIfIsAccepted("aa", "aa");
        testIfIsAccepted("aaa", "aaa");
        testIfIsAccepted("aaaa", "aaaa");
        testIfIsAccepted("aaaaa", "aaaa");
    }


    @Test
    public void testOr() {
        with(Regex.text("ab").or(Regex.text("ac")));
        testIfIsAccepted("ab", "ab");
        testIfIsAccepted("ac", "ac");
        testIfIsAccepted("bc", null);
        testIfIsAccepted("aa", null);
    }
}
