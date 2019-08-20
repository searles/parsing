package at.searles.lexer;

import at.searles.lexer.utils.IntSet;
import at.searles.regex.Regex;

public class LexerWithHidden implements Tokenizer {

    private final Lexer lexer;
    private final IntSet hiddenTokenIds;

    public LexerWithHidden(Lexer lexer) {
        this.lexer = lexer;
        this.hiddenTokenIds = new IntSet();
    }

    public LexerWithHidden() {
        this(0);
    }

    public LexerWithHidden(int tokIdOffset) {
        this(new Lexer(tokIdOffset));
    }

    public void addTokenIdToHidden(int tokId) {
        hiddenTokenIds.add(tokId);
    }

    public int addHiddenToken(Regex regex) {
        int tokId = add(regex);
        addTokenIdToHidden(tokId);
        return tokId;
    }

    @Override
    public int add(Regex regex) {
        return lexer.add(regex);
    }

    @Override
    public IntSet parseToken(TokStream stream) {
        for (;;) {
            IntSet acceptedTokIds = stream.fetchTokenIds(lexer);

            if (acceptedTokIds == null) {
                return null;
            }

            int hiddenIndex = acceptedTokIds.indexOfFirstMatch(hiddenTokenIds);

            if (hiddenIndex == -1) {
                return acceptedTokIds;
            }

            // hidden token. Report first match.
            stream.markConsumed(acceptedTokIds.getAt(hiddenIndex));
        }
    }
}
