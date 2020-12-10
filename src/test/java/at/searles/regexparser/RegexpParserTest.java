package at.searles.regexparser;

import at.searles.regexp.CharSet;
import at.searles.regexp.Regexp;
import org.junit.Assert;
import org.junit.Test;

public class RegexpParserTest {
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
    public void testUnionCharSet() {
        CodePointStream stream = new CodePointStream("[^ac-e-g\\]0-9]");

        CharSet set = CharSetParser.charSet(stream);

        Assert.assertFalse(set.contains('-'));
        Assert.assertTrue(set.contains('b'));
        Assert.assertFalse(set.contains(']'));
    }

    @Test
    public void testUnion() {
        CodePointStream stream = new CodePointStream("'a' | 'b'");

        RegexpParser.union(stream);

        Assert.assertTrue(stream.end());
    }

    @Test
    public void testComment() {
        CodePointStream stream = new CodePointStream("'//' [^\\n]*");

        RegexpParser.union(stream);

        Assert.assertTrue(stream.end());
    }
}