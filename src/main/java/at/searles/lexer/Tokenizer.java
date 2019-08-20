package at.searles.lexer;

import at.searles.lexer.utils.IntSet;
import at.searles.regex.Regex;

public interface Tokenizer {
    // only in Lexer. IntSet nextToken(TokStream stream);

    /**
     * Simplified for creating a token from a simple string.
     *
     */
    default Token token(String str) {
        return token(Regex.text(str));
    }

    /**
     * Creates a token from a regex
     *
     * @param rex the regex
     * @return the token
     */
    default Token token(Regex rex) {
        int tokId = add(rex);
        return new Token(this, tokId);
    }

    /**
     * Add a regular expression
     *
     * @param regex The regular expression
     * @return the token id.
     */
    int add(Regex regex);

    /**
     * Tries to parse the token with the given id and on success returns the
     * matching frame. In the simplest case it is a call to stream.fetchTokenIds,
     * but if the Tokenizer should ignore some tokens this is the place.
     * If only this method is used, stream.markConsumed() must be called.
     * Therefore, it is recommended to rather use the methods in the Token class.
     * @return null if unsuccessful
     */
    IntSet parseToken(TokStream stream);
}
