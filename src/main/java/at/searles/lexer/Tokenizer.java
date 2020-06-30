package at.searles.lexer;

import at.searles.buf.Frame;
import at.searles.lexer.utils.IntSet;
import at.searles.regexp.Regexp;

public interface Tokenizer {

    IntSet currentTokenIds(TokenStream stream);

    /**
     * Returns whether this tokenizer accepts the current element
     * in the token stream. If so, the token stream continues
     * to the next element.
     * @return null if the tokenizer does not recognize this element.
     */
    default Frame matchToken(TokenStream stream, int tokId) {
        IntSet currentTokenIds = currentTokenIds(stream);

        if(currentTokenIds != null && currentTokenIds.contains(tokId)) {
            stream.advance(tokId);
            return stream.frame();
        }

        return null;
    }

    int add(Regexp regexp);

    Lexer lexer();
}
