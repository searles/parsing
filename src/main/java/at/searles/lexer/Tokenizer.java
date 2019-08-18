package at.searles.lexer;

import at.searles.lexer.utils.IntSet;
import at.searles.regex.Regex;

public interface Tokenizer {
    IntSet nextToken(TokStream stream);

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
     * Optional. Add a regular expression
     *
     * @param regex The regular expression
     * @return the token id.
     */
    int add(Regex regex);
}
