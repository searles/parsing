package at.searles.lexer

import at.searles.buf.Frame
import at.searles.lexer.utils.Counter
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class TokenStreamListenerTest {
    var lastEnd: Long = 0

    @Before
    fun setUp() {
        lastEnd = 0
    }

    @Test
    fun testTokenSequence() {
        // setup
        val lexer = Lexer()
        val tokenizer = SkipTokenizer(lexer)
        val aId = lexer.add("a")
        val bId = lexer.add("b")
        tokenizer.addSkipped(bId)
        val stream = TokenStream.fromString("abaababb")
        val counter = Counter()

        stream.listener = object: TokenStream.Listener {
            override fun tokenConsumed(src: TokenStream, tokenId: Int, frame: Frame) {
                Assert.assertEquals(lastEnd, frame.start)
                lastEnd = frame.end
                counter.incr()
            }
        }

        for (i in 0..3) {
            Assert.assertNotNull(tokenizer.matchToken(stream, aId))
        }
        Assert.assertNull(tokenizer.matchToken(stream, aId))
        Assert.assertEquals(8, counter.get().toLong())
    }
}