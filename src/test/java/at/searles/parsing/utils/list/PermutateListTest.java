package at.searles.parsing.utils.list;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class PermutateListTest {

    @Test
    public void parse() {
        PermutateList<String> mapping = new PermutateList<>(1, 2, 0);

        List<String> result = mapping.parse(null, null, Arrays.asList("A", "B", "C"));

        Assert.assertEquals(Arrays.asList("B", "C", "A"), result);
    }

    @Test
    public void left() {
        PermutateList<String> mapping = new PermutateList<>(1, 2, 0);

        List<String> result = mapping.left(null, Arrays.asList("A", "B", "C"));

        Assert.assertEquals(Arrays.asList("C", "A", "B"), result);
    }
}