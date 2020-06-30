package at.searles.lexer.test;

import at.searles.lexer.Lexer;
import at.searles.lexer.SkipTokenizer;
import at.searles.lexer.TokenStream;
import at.searles.lexer.utils.Counter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TokenStreamListenerTest {
    long lastEnd;

    @Before
    public void setUp() {
        lastEnd = 0;
    }

    @Test
    public void testTokenSequence() {
        // setup
        Lexer lexer = new Lexer();
        SkipTokenizer tokenizer = new SkipTokenizer(lexer);

        int aId = lexer.add("a");
        int bId = lexer.add("b");

        tokenizer.addSkipped(bId);

        TokenStream stream = TokenStream.fromString("abaababb");

        Counter counter = new Counter();

        stream.setListener((src, tokId, frame) -> {
            Assert.assertEquals(lastEnd, frame.startPosition());
            lastEnd = frame.endPosition();
            counter.incr();
        });

        for(int i = 0; i < 4; ++i) {
            Assert.assertNotNull(tokenizer.matchToken(stream, aId));
        }

        Assert.assertNull(tokenizer.matchToken(stream, aId));
        Assert.assertEquals(8, counter.get());
    }
}
