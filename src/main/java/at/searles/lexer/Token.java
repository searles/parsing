package at.searles.lexer;

import at.searles.buf.FrameStream;
import at.searles.lexer.utils.IntSet;

/**
 * A token is
 */
public class Token {

    public final Tokenizer tokenizer;
    public final int tokId;

    public Token(Tokenizer tokenizer, int tokId) {
        this.tokId = tokId;
        this.tokenizer = tokenizer;
    }

    public boolean recognizeToken(TokStream tokStream) {
        return recognizeToken(tokStream, false);
    }

    /**
     * Returns true if the next element in tokstream matches this token.
     */
    public boolean recognizeToken(TokStream tokStream, boolean exclusive) {
        return parseToken(tokStream, exclusive) != null;
    }

    /**
     * Returns a char sequence (that is always an instance of TokenSet) that matches
     * this token.
     *
     * Single entry point for parsing token.
     * @param stream The TokStream from which the token is fetched
     * @param exclusive if true, then no other token is allowed to match. Use eg for identifiers
     *                  that should not be confused with keywords.
     * @return null, if the next item is not a Token.
     */
    public FrameStream.Frame parseToken(TokStream stream, boolean exclusive) {
        // Fetch next (current) token.
        IntSet acceptedTokIds = tokenizer.parseToken(stream);

        if(acceptedTokIds == null) {
            return null;
        }

        if(exclusive && acceptedTokIds.size() != 1) {
            return null;
        }

        if(!acceptedTokIds.contains(tokId)) {
            return null;
        }

        // alright, we got a match.
        stream.markConsumed(tokId);

        return stream.frame();
    }

    public String toString() {
        return String.format("<%d>", tokId);
    }
}
