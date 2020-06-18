package at.searles.buf.test;

import at.searles.buf.FrameStream;
import at.searles.buf.StringWrapper;
import at.searles.lexer.fsa.FSA;
import at.searles.lexer.utils.Counter;
import at.searles.regexp.CharSet;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class FSATest {

    private Counter counter;
    private FSA fsa;
    private String s;

    @Before
    public void setup() {
        this.counter = new Counter();
    }

    private void withFSA(FSA fsa) {
        this.fsa = fsa;
    }

    private void withString(String s) {
        this.s = s;
    }

    private FSA step(int... chs) {
        return new FSA(counter, CharSet.chars(chs));
    }

    private void assertFullMatch(boolean match, boolean full) {
        FrameStream stream = new StringWrapper(s);

        if (match) {
            Assert.assertNotNull(fsa.accept(stream));
        } else {
            Assert.assertNull(fsa.accept(stream));
        }

        if (match) Assert.assertEquals(full, stream.frame().toString().equals(s));
    }

    @Test
    public void testThenForOptionalPlusFSA() {
        // Two FSAs with cycles.
        // q1 -a-> q2[term] -b-> q1
        FSA fsaLeft = step('a').then(step('b')).plus();
        FSA fsaRight = step('b').then(step('a')).plus().opt();

        withFSA(fsaLeft.then(fsaRight));

        withString("ababbaba");
        assertFullMatch(true, true);

        withString("abab");
        assertFullMatch(true, true);

        withString("abba");
        assertFullMatch(true, true);

        withString("aa");
        assertFullMatch(false, false);

        withString("aba");
        assertFullMatch(true, false);
    }

    @Test
    public void testThenForNonOptionalPlusFSA() {
        FSA fsaLeft = step('a').then(step('b')).plus();
        FSA fsaRight = step('b').then(step('a')).plus();

        withFSA(fsaLeft.then(fsaRight));

        withString("ababbaba");
        assertFullMatch(true, true);

        withString("abab");
        assertFullMatch(false, false);

        withString("abba");
        assertFullMatch(true, true);
    }

    @Test
    public void testSequenceOfOptionals() {
        FSA fsaLeft = step('a').opt();
        FSA fsaRight = step('a').opt();

        withFSA(fsaLeft.then(fsaRight));

        withString("aaa");
        assertFullMatch(true, false);

        withString("aa");
        assertFullMatch(true, true);

        withString("a");
        assertFullMatch(true, true);

        withString("");
        assertFullMatch(true, true);
    }

    @Test
    public void testSequenceOfOptionalsDifferentChars() {
        FSA fsaLeft = step('a').opt();
        FSA fsaRight = step('b').opt();

        withFSA(fsaLeft.then(fsaRight));

        withString("aba");
        assertFullMatch(true, false);

        withString("ab");
        assertFullMatch(true, true);

        withString("a");
        assertFullMatch(true, true);

        withString("b");
        assertFullMatch(true, true);
    }

    @Test
    public void testRepAutomaton() {
        withFSA(step('a').plus());

        withString("aaaaa");
        assertFullMatch(true, true);

        withString("aaaaab");
        assertFullMatch(true, false);

        withString("a");
        assertFullMatch(true, true);
    }

    @Test
    public void testOneStepAutomaton() {
        withFSA(step('a'));

        withString("a");
        assertFullMatch(true, true);

        withString("b");
        assertFullMatch(false, false);
    }

    @Test
    public void testOneStepAutomatonProperties() {
        withFSA(step('a'));

        Assert.assertEquals(1, fsa.accepting().size());
    }

    @Test
    public void testTwoStepAutomaton() {
        withFSA(step('a').then(step('b')));

        withString("ab");
        assertFullMatch(true, true);

        withString("a");
        assertFullMatch(false, false);

        withString("b");
        assertFullMatch(false, false);
    }

    @Test
    public void testOrAutomaton() {
        withFSA(step('a').or(step('b')));

        withString("c");
        assertFullMatch(false, false);

        withString("a");
        assertFullMatch(true, true);

        withString("aa");
        assertFullMatch(true, false);

        withString("b");
        assertFullMatch(true, true);

        withString("bb");
        assertFullMatch(true, false);
    }

}
