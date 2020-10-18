package at.searles.lexer;

import at.searles.lexer.utils.IntSet;
import at.searles.regexp.CharSet;
import at.searles.regexp.Regexp;
import at.searles.regexp.Text;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test for basic lexer functionality.
 */
public class LexerTest {

    private Lexer lexer;
    private int token;

    private void with(Regexp regexp) {
        lexer = new Lexer();
        token = lexer.add(regexp);
    }

    private void testIfIsAccepted(String string, String expected) {
        TokenStream stream = TokenStream.Companion.fromString(string);

        IntSet tokIds = stream.getAcceptedTokens(lexer);

        Assert.assertEquals(expected != null, tokIds != null);
        Assert.assertEquals(expected != null, tokIds != null && tokIds.contains(token));
        Assert.assertEquals(expected, tokIds != null ? stream.getFrame().toString() : null);
    }

    @Test
    public void testRange02() {
        with(CharSet.Companion.chars('a').range(0, 2));
        testIfIsAccepted("b", "");
        testIfIsAccepted("a", "a");
        testIfIsAccepted("aa", "aa");
        testIfIsAccepted("aaa", "aa");
    }

    @Test
    public void testRange13() {
        with(CharSet.Companion.chars('a').range(1, 3));
        testIfIsAccepted("b", null);
        testIfIsAccepted("a", "a");
        testIfIsAccepted("aa", "aa");
        testIfIsAccepted("aaa", "aaa");
        testIfIsAccepted("aaaa", "aaa");
    }

    @Test
    public void testRange23() {
        with(CharSet.Companion.chars('a').range(2, 3));
        testIfIsAccepted("b", null);
        testIfIsAccepted("a", null);
        testIfIsAccepted("aa", "aa");
        testIfIsAccepted("aaa", "aaa");
        testIfIsAccepted("aaaa", "aaa");
    }

    @Test
    public void testRange24() {
        with(CharSet.Companion.chars('a').range(2, 4));
        testIfIsAccepted("b", null);
        testIfIsAccepted("a", null);
        testIfIsAccepted("aa", "aa");
        testIfIsAccepted("aaa", "aaa");
        testIfIsAccepted("aaaa", "aaaa");
        testIfIsAccepted("aaaaa", "aaaa");
    }


    @Test
    public void testOr() {
        with(new Text("ab").or(new Text("ac")));
        testIfIsAccepted("ab", "ab");
        testIfIsAccepted("ac", "ac");
        testIfIsAccepted("bc", null);
        testIfIsAccepted("aa", null);
    }
}
