package at.searles.buf.test;

import at.searles.buf.StringWrapper;
import org.junit.Assert;
import org.junit.Test;

public class StringWrapperTest {
    private StringWrapper str;

    @Test
    public void initialConditionTest() {
        withString("basic");

        Assert.assertEquals(0, str.position());
        Assert.assertEquals(0, str.frame().length());
    }

    @Test
    public void koalaTest() {
        withString("\uD83D\uDC28koala"); // first char is the unicode koala
        Assert.assertEquals(0x1f428, str.next()); // UTF-16
        Assert.assertEquals('k', str.next());

        str.mark();

        Assert.assertEquals('o', str.next());

        str.advance();

        Assert.assertEquals('o', str.next());
    }

    @Test
    public void frameTest() {
        withString("abcdef"); // first char is the unicode koala

        str.next();
        str.next();

        str.mark();

        str.advance();

        str.next();
        str.next();

        str.mark();

        str.next();

        Assert.assertEquals("cd", str.frame().toString());
    }

    @Test
    public void setPtrTest() {
        withString("abcdef"); // first char is the unicode koala

        str.next();
        str.next();

        str.mark();

        str.advance();

        str.next();
        str.next();

        str.mark();

        str.next();

        str.setPositionTo(0);

        str.next();
        str.next();

        str.mark();

        str.next();

        Assert.assertEquals("ab", str.frame().toString());
    }

    private void withString(String str) {
        this.str = new StringWrapper(str);
    }
}
