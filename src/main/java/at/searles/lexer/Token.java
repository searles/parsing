package at.searles.lexer;

/**
 * A token is
 */
public class Token {

    public final Tokenizer lexer;
    public final int tokIndex;

    public Token(Tokenizer lexer, int tokIndex) {
        this.tokIndex = tokIndex;
        this.lexer = lexer;
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
     * @param tokStream The TokStream from which the token is fetched
     * @param exclusive if true, then no other token is allowed to match. Use eg for identifiers
     *                  that should not be confused with keywords.
     * @return null, if the next item is not a Token.
     */
    public CharSequence parseToken(TokStream tokStream, boolean exclusive) {
        // Fetch next (current) token.
        if (!tokStream.fetchToken(lexer)) {
            return null;
        }

        if (exclusive && tokStream.acceptedTokens().size() != 1) {
            return null;
        }

        if (!tokStream.acceptedTokens().contains(tokIndex)) {
            return null;
        }

        // mark token as consumed
        tokStream.flushToken();

        return tokStream.frame();
    }

    public String toString() {
        return String.format("<%d>", tokIndex);
    }
}
