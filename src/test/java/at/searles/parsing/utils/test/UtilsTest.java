package at.searles.parsing.utils.test;

import at.searles.parsing.Fold;
import at.searles.parsing.utils.list.Append;
import at.searles.parsing.utils.list.Prepend;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class UtilsTest {
    @Test
    public void testAppendMayBeEmpty() {
        Fold<List<Integer>, Integer, List<Integer>> cons = new Append<>(0);

        List<Integer> l = Arrays.asList(1, 2);

        Assert.assertEquals((Integer) 2, cons.rightInverse(null, l));
        l = cons.leftInverse(null, l);
        Assert.assertEquals((Integer) 1, cons.rightInverse(null, l));
        l = cons.leftInverse(null, l);

        Assert.assertNotNull(l);
        Assert.assertTrue(l.isEmpty());

        Assert.assertNull(cons.leftInverse(null, l));
        Assert.assertNull(cons.rightInverse(null, l));
    }

    @Test
    public void testAppendMayNotBeEmpty() {
        Fold<List<Integer>, Integer, List<Integer>> cons = new Append<>(1);

        List<Integer> l = Arrays.asList(1, 2);

        Assert.assertEquals((Integer) 2, cons.rightInverse(null, l));
        l = cons.leftInverse(null, l);

        Assert.assertEquals(1, l.size());

        Assert.assertNull(cons.leftInverse(null, l));
        Assert.assertNull(cons.rightInverse(null, l));
    }

    @Test
    public void testPrepend() {
        Fold<Integer, List<Integer>, List<Integer>> cons = new Prepend<>();

        List<Integer> l = Arrays.asList(1, 2);

        Assert.assertEquals((Integer) 1, cons.leftInverse(null, l));
        l = cons.rightInverse(null, l);
        Assert.assertEquals((Integer) 2, cons.leftInverse(null, l));
        l = cons.rightInverse(null, l);

        Assert.assertNotNull(l);
        Assert.assertTrue(l.isEmpty());

        Assert.assertNull(cons.leftInverse(null, l));
        Assert.assertNull(cons.rightInverse(null, l));
    }
}
