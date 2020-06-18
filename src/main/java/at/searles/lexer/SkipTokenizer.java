package at.searles.lexer;

import at.searles.lexer.utils.IntSet;
import at.searles.regexp.Regexp;

public class SkipTokenizer implements Tokenizer {

    private final IntSet skippedTokenIds;
    private final Tokenizer parent;

    public SkipTokenizer(Tokenizer parent) {
        this.parent = parent;
        this.skippedTokenIds = new IntSet();
    }

    public Lexer lexer() {
        return parent.lexer();
    }

    public void addSkipped(int tokId) {
        skippedTokenIds.add(tokId);
    }

    @Override
    public IntSet currentTokenIds(TokenStream stream) {
        IntSet currentTokenIds = parent.currentTokenIds(stream);

        while(currentTokenIds != null) {
            int index = currentTokenIds.indexOfFirstMatch(skippedTokenIds);

            if(index == -1) {
                // not a hidden symbol.
                break;
            }

            stream.advance(currentTokenIds.getAt(index));

            currentTokenIds = parent.currentTokenIds(stream);
        }

        return currentTokenIds;
    }

    @Override
    public int add(Regexp regexp) {
        return parent.add(regexp);
    }
}
