package at.searles.lexer;

import at.searles.lexer.utils.IntSet;
import at.searles.regex.Regex;

/**
 * This tokenizer can be used to implement soft keywords like
 * they are allowed in kotlin.
 * Eg. a tokenizer contains identifiers, 'if' and 'public'.
 * 'public' is not a hard keyword, ie, in some contexts like
 * local variable names it is allowed as an identifier.
 * In this case, the shadowed tokenizer can hide
 * the public-match.
 * Careful not to use it on tokens that are exclusive
 * themselves. Eg this cannot be used to match '>>' as
 * shiftright and still allow java-alike type notations
 * like <code><A<B>></code> because '>>' would still be the longest
 * match. In this latter case, use a separate lexer.
 */
public class ShadowedTokenizer implements Tokenizer {

    private final IntSet shadowedTokenIds;
    private final Tokenizer parent;

    public ShadowedTokenizer(Tokenizer parent) {
        this.parent = parent;
        shadowedTokenIds = new IntSet();
    }

    @Override
    public IntSet currentTokenIds(TokenStream stream) {
        return parent.currentTokenIds(stream);
    }

    public void addShadowed(int tokenId) {
        shadowedTokenIds.add(tokenId);
    }

    @Override
    public boolean isExclusiveToken(IntSet tokenIds) {
        int count = 0;

        for(int i = 0; i < tokenIds.size() && count <= 1; ++i) {
            if(!shadowedTokenIds.contains(tokenIds.getAt(i))) {
                count++;
            }
        }

        return count == 1;
    }

    @Override
    public int add(Regex regex) {
        return parent.add(regex);
    }

    @Override
    public Lexer lexer() {
        return parent.lexer();
    }
}
