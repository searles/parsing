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

    /**
     * underlying buffer
     */
    private final FrameStream stream;

    /**
     * Values to determine current match
     */
    private Tokenizer tokenizer;
    private IntSet acceptedTokens;
    private boolean isConsumed;
    private Listener listener;

    public TokStream(FrameStream stream) {
        this.stream = stream;

        this.tokenizer = null;
        this.acceptedTokens = null;

        this.isConsumed = true;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
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
        if (!isConsumed && this.tokenizer == tokenizer) {
            // nothing to do.
            return acceptedTokens != null;
        }

        if (isConsumed) {
            // the last token was consumed, so flush it.
            stream.flushFrame();
        } else {
            // last token was not consumed or we are at the beginning.
            stream.resetFrame();
        }

        this.tokenizer = tokenizer;
        this.acceptedTokens = this.tokenizer.nextToken(this);
        this.isConsumed = false;

        return acceptedTokens != null;

    }

    /**
     * must be called before advancing to the next token.
     * frame will remain valid until increment is called.
     * @param tokId The id of the actuallu consumed token
     */
    public void markConsumed(int tokId) {
        this.isConsumed = true;

        // inform listeners
        notifyTokenConsumed(tokenizer, tokId, frame());
    }

    public void notifyTokenConsumed(Tokenizer tokenizer, int tokId, CharSequence frame) {
        if(listener != null) {
            listener.tokenConsumed(tokenizer, tokId, frame);
        }
    }

    // This is kinda a read-only-frameStream.

    public FrameStream frameStream() {
        return stream;
    }

    public long frameStart() {
        return stream.frameStart();
    }

    public long frameEnd() {
        return stream.frameEnd();
    }

    public CharSequence frame() {
        return stream.frame();
    }

    public boolean isExclusivelyAccepted() {
        return acceptedTokens != null && acceptedTokens.size() == 1;
    }

    /**
     * Before calling this method, fetchToken(Tokenizer) must have
     * been called to ensure that the correct tokenizer is set.
     */
    public boolean isAcceptedToken(int tokId) {
        return acceptedTokens != null && acceptedTokens.contains(tokId);
    }

    @Override
    public String toString() {
        return stream.toString();
    }

    public interface Listener {
        void tokenConsumed(Tokenizer tokenizer, int tokId, CharSequence frame);
    }
}
