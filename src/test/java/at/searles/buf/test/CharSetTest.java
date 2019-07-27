package at.searles.buf.test;

import at.searles.lexer.utils.Interval;
import at.searles.regex.CharSet;
import org.junit.Assert;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

public class CharSetTest {
    private CharSet set;

    @Test
    public void testCharSetInvert() {
        with(CharSet.chars('.'));

        set = set.invert();

        Assert.assertTrue(set.isInverted());

        List<Interval> l = new LinkedList<>();

        for (Interval i : set) {
            l.add(i);
        }

        Assert.assertEquals(2, l.size());
        Assert.assertEquals('.', l.get(0).end);
        Assert.assertEquals('.' + 1, l.get(1).start);
    }

    private void with(CharSet set) {
        this.set = set;
    }
}
