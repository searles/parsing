package at.searles.lexer;

import at.searles.buf.FrameStream;
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
        this(new Lexer());
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
    public IntSet nextToken(FrameStream stream) {
        for (;;) {
            IntSet tokIds = lexer.nextToken(stream);

            if (tokIds == null) {
                return null;
            }

            if (!tokIds.containsAny(hiddenTokenIds)) {
                return tokIds;
            }

            // hidden token. flush it.
            // FIXME how to notify?
            stream.flushFrame();
        }
    }
}
