package at.searles.lexer;

import at.searles.buf.*;
import at.searles.lexer.utils.IntSet;

/**
 * This wrapper around a FrameStream reads from the frame stream
 * as long as characters can be fetched from the used lexer.
 * The internal fields then indicate which tokens in the given matcher
 * were fetched.
 * <p>
 * If the token then is consumed, the underlying frame stream
 * is flushed so that it is ready for the next token.
 * <p>
 * If no token could be found, then the token set is empty.
 * <p>
 * If there is a match but it is not used, the reset-method
 * will revert all changes.
 */
public class TokStream {

    // underlying buffer
    private final FrameStream stream;
    /**
     * Values to determine current match
     */
    private Tokenizer tokenizer;
    private IntSet acceptedTokens;

    public TokStream(FrameStream stream) {
        this.stream = stream;

        this.tokenizer = null;
        this.acceptedTokens = null;
    }

    public static TokStream fromString(String s) {
        return new TokStream(new StringWrapper(s));
    }

    public static TokStream fromCharStream(CharStream stream) {
        return new TokStream(new FrameStreamImpl(new BufferedStream.Impl(stream, 1024)));
    }

    public void setPtr(long ptr) {
        this.acceptedTokens = null;
        this.stream.resetFrame();
        this.tokenizer = null;

        stream.setPtr(ptr);
    }

    /**
     * @return Returns the start ptr of the next token that will be parsed.
     */
    public long offset() {
        // if lexer == null, use frameEnd because framestream still captures last match.
        // if lexer is set, use frameStart.
        return tokenizer == null ? stream.frameEnd() : stream.frameStart();
    }

    /**
     * If the current token has not been consumed or a different lexer
     * is used, this method does nothing except for returning
     * whether a valid token has been fetched. Otherwise, it sets the
     * currently accepted tokens-field and sets the correct frame
     * in the underlying frameStream.
     */
    public boolean fetchToken(Tokenizer tokenizer) {
        if (this.tokenizer == tokenizer) {
            // nothing to do.
            return acceptedTokens != null;
        }

        if (this.tokenizer == null) {
            // the last token was flushed
            stream.flushFrame();
        } else {
            // last token was not consumed or we are at the beginning.
            stream.resetFrame();
        }

        this.tokenizer = tokenizer;
        this.acceptedTokens = this.tokenizer.nextToken(stream);

        return acceptedTokens != null;
    }

    /**
     * must be called before advancing to the next token.
     * frame will remain valid until increment is called.
     */
    public void flushToken() {
        this.tokenizer = null;
    }

    public long frameStart() {
        return stream.frameStart();
    }

    public long frameEnd() {
        return stream.frameEnd();
    }

    /**
     * Do not modify the set; otherwise the lexer will return
     * wrong results in subsequent calls.
     *
     * @return The intset of accepted tokens from the last
     * fetchToken-Call.
     */
    public IntSet acceptedTokens() {
        return acceptedTokens;
    }

    public CharSequence frame() {
        return stream.frame();
    }

    @Override
    public String toString() {
        return stream.toString();
    }
}
