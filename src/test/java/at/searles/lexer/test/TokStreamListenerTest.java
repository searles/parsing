package at.searles.lexer.test;

import at.searles.lexer.LexerWithHidden;
import at.searles.lexer.TokStream;
import at.searles.lexer.utils.Counter;
import at.searles.lexer.utils.IntSet;
import at.searles.regex.Regex;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TokStreamListenerTest {
    long lastEnd;

    @Before
    public void setUp() {
        lastEnd = 0;
    }

    @Test
    public void testConsecuitveTokens() {
        // setup
        LexerWithHidden lexer = new LexerWithHidden();

        int aId = lexer.add(Regex.text("a"));
        int bId = lexer.addHiddenToken(Regex.text("b"));

        TokStream stream = TokStream.fromString("abaababb");

        Counter counter = new Counter();

        stream.setListener((tokId, frame) -> {
            Assert.assertEquals(lastEnd, frame.startPosition());
            lastEnd = frame.endPosition();
            counter.incr();
        });

        for(int i = 0; i < 4; ++i) {
            IntSet intSet = lexer.parseToken(stream);
            Assert.assertEquals(1, intSet.size());
            Assert.assertTrue(intSet.contains(aId));

            stream.markConsumed(aId);
        }

        Assert.assertNull(lexer.parseToken(stream));
        Assert.assertEquals(8, counter.get());
    }
}
