package at.searles.regex.parser;

import at.searles.regex.CharSet;
import org.junit.Assert;
import org.junit.Test;

public class StringToRegexTest {
    @Test
    public void testRawString() {
        CodePointStream stream = new CodePointStream("'a\\b\\\\c\\'d'");

        String string = RawStringParser.fetch(stream);

        Assert.assertEquals("a\\b\\c'd", string);

        string = RawStringParser.unparse("\\a'");

        Assert.assertEquals("'\\\\a\\''", string);
    }

    @Test
    public void testCharSet() {
        CodePointStream stream = new CodePointStream("[^ac-e-g\\]0-9]");

        CharSet set = CharSetParser.charSet(stream);

        Assert.assertFalse(set.contains('-'));
        Assert.assertTrue(set.contains('b'));
        Assert.assertFalse(set.contains(']'));
    }


    @Test
    public void testUnion() {
        CodePointStream stream = new CodePointStream("[^ac-e-g\\]0-9]");

        CharSet set = CharSetParser.charSet(stream);

        Assert.assertFalse(set.contains('-'));
        Assert.assertTrue(set.contains('b'));
        Assert.assertFalse(set.contains(']'));
    }

}