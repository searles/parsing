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
public class TokenStream {

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

    public TokenStream(FrameStream stream) {
        this.stream = stream;

        this.lexer = null;
        this.acceptedTokens = null;

        this.isConsumed = true;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public static TokenStream fromString(CharSequence s) {
        return new TokenStream(new StringWrapper(s));
    }

    public static TokenStream fromCharStream(CharStream stream) {
        return new TokenStream(new FrameStreamImpl(new BufferedStream.Impl(stream, 1024)));
    }

    public void setPositionTo(long ptr) {
        this.stream.reset();

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
     *
     * @return null if there is no accepted token. This
     * is not equivalent to EOF, because EOF is just
     * defined as '-1' and thus can be recognized by
     * a lexer.
     */
    public IntSet current(Lexer lexer) {
        // can we reuse the old result?
        if(!isConsumed && this.lexer == lexer) {
            return this.acceptedTokens;
        }

        // no

        if (isConsumed) {
            stream.advance();
        } else /* if(this.lexer != lexer) */ {
            stream.reset();
        }

        this.isConsumed = false;
        this.lexer = lexer;
        this.acceptedTokens = lexer.readNextToken(stream);

        return this.acceptedTokens;
    }

    /**
     * A lazy advance method like in C iterators. Only the
     * next call to 'current' will actually advance. This
     * is because the frame must remain valid for now.
     *
     * @param consumedTokenId The id of the actually consumed token
     */
    public void advance(int consumedTokenId) {
        if(this.isConsumed) {
            // last was not consumed.
            // well, I guess a 'skip 3 tokens'
            // can be useful...
            current(lexer);
        }

        this.isConsumed = true;

        // inform listeners
        notifyTokenConsumed(consumedTokenId, stream.frame());
    }

    private void notifyTokenConsumed(int tokId, Frame frame) {
        if(listener != null) {
            listener.tokenConsumed(this, tokId, frame);
        }
    }

    @Override
    public String toString() {
        return stream.toString();
    }

    public Frame frame() {
        return stream.frame();
    }

    public interface Listener {
        void tokenConsumed(TokenStream src, int tokId, Frame frame);
    }
}
