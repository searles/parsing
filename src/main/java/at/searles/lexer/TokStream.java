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
    private Lexer lexer;
    private IntSet acceptedTokens;

    private boolean isConsumed;
    private Listener listener;

    public TokStream(FrameStream stream) {
        this.stream = stream;

        this.lexer = null;
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

    public void setPositionTo(long ptr) {
        this.stream.resetFrame();

        this.acceptedTokens = null;
        this.lexer = null;

        stream.setPositionTo(ptr);
    }

    /**
     * @return Returns the start position of the next token that will be parsed.
     */
    public long offset() {
        // if lexer == null, use endPosition because framestream still captures last match.
        // if lexer is set, use startPosition.
        return isConsumed ? stream.frame().endPosition() : stream.frame().startPosition();
    }

    /**
     * If the current token has not been consumed or a different lexer
     * is used, this method does nothing except for returning
     * whether a valid token has been fetched. Otherwise, it sets the
     * currently accepted tokens-field and sets the correct frame
     * in the underlying frameStream.
     */
    public IntSet fetchTokenIds(Lexer lexer) {
        // can we reuse the old result?
        if (isConsumed || this.lexer != lexer) {
            if (isConsumed) {
                stream.advanceFrame();
                isConsumed = false;
            } else /* if(this.lexer != lexer) */ {
                stream.resetFrame();
            }

            this.acceptedTokens = lexer.nextToken(stream);
            this.lexer = lexer;
            this.isConsumed = false;
        }

        return this.acceptedTokens;
    }

    /**
     * must be called before advancing to the next token.
     * frame will remain valid until increment is called.
     * @param tokId The id of the actuallu consumed token
     */
    public void markConsumed(int tokId) {
        this.isConsumed = true;

        // inform listeners
        notifyTokenConsumed(tokId, stream.frame());
    }

    public void notifyTokenConsumed(int tokId, FrameStream.Frame frame) {
        if(listener != null) {
            listener.tokenConsumed(this, tokId, frame);
        }
    }

    // This is kinda a read-only-frameStream.

    @Override
    public String toString() {
        return stream.toString();
    }

    public FrameStream.Frame frame() {
        return stream.frame();
    }

    public interface Listener {
        void tokenConsumed(TokStream src, int tokId, FrameStream.Frame frame);
    }
}
