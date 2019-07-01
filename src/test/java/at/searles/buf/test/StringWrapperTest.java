package at.searles.buf.test;

import at.searles.buf.StringWrapper;
import org.junit.Assert;
import org.junit.Test;

public class StringWrapperTest {
    private StringWrapper str;

    @Test
    public void initialConditionTest() {
        withString("basic");

        Assert.assertEquals(0, str.ptr());
        Assert.assertEquals(0, str.frame().length());
    }

    @Test
    public void koalaTest() {
        withString("\uD83D\uDC28koala"); // first char is the unicode koala
        Assert.assertEquals(0x1f428, str.next()); // UTF-16
        Assert.assertEquals('k', str.next());

        str.markFrameEnd();

        Assert.assertEquals('o', str.next());

        str.flushFrame();

        Assert.assertEquals('o', str.next());
    }

    @Test
    public void frameTest() {
        withString("abcdef"); // first char is the unicode koala

        str.next();
        str.next();

        str.markFrameEnd();

        str.flushFrame();

        str.next();
        str.next();

        str.markFrameEnd();

        str.next();

        Assert.assertEquals("cd", str.frame().toString());
    }

    @Test
    public void setPtrTest() {
        withString("abcdef"); // first char is the unicode koala

        str.next();
        str.next();

        str.markFrameEnd();

        str.flushFrame();

        str.next();
        str.next();

        str.markFrameEnd();

        str.next();

        str.setPtr(0);

        str.next();
        str.next();

        str.markFrameEnd();

        str.next();

        Assert.assertEquals("ab", str.frame().toString());
    }

    private void withString(String str) {
        this.str = new StringWrapper(str);
    }
}
