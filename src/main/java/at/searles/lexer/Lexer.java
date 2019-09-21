package at.searles.lexer;

import at.searles.buf.FrameStream;
import at.searles.lexer.fsa.FSA;
import at.searles.lexer.utils.Counter;
import at.searles.lexer.utils.IntSet;
import at.searles.regex.Regex;

import java.util.ArrayList;

/**
 * Pretty much a more useable frontend for FSA. A lexer reads tokens from a buf.
 * It always finds the longest match. Each regex that is added returns a unique
 * integer. Multiple regexes can match an element (for instance, a keyword is
 * usually also an ID). In this case, both elements match.
 * The lexer does not take care of hidden tokens, this is the duty of
 * TokStream.
 */
public class Lexer implements Tokenizer {

    private static final int RESERVATION = Integer.MIN_VALUE; // mark nodes that will be added for the next token.
    /**
     * Data that is shared amongst all FSAs that are generated in this lexer.
     */
    final Counter fsaNodeCounter = new Counter();
    /**
     * fsa that accepts our current language. Empty language is not allowed,
     * add must be called at least once.
     */
    private final FSA fsa;

    /**
     * Counter used to get unique IDs. If multiple lexers
     * are used, they should share a counter to avoid
     * collisions of IDs.
     */
    private Counter tokIdOffset;

    public Lexer(Counter tokIdOffset) {
        if(tokIdOffset.get() <= RESERVATION) {
            throw new IllegalArgumentException("Bad token id offset");
        }

        this.tokIdOffset = tokIdOffset;

        this.fsa = new FSA(this.fsaNodeCounter, false); // accept nothing.
    }

    public Lexer() {
        this(new Counter());
    }

    @Override
    public IntSet currentTokenIds(TokenStream stream) {
        return stream.current(this);
    }

    public Counter getTokenIdOffset() {
        return tokIdOffset;
    }

    private FSA regexToFsa(Regex regex) {
        // Create an fsa for the lexer. Coupling is bad here btw...
        return regex.accept(new LexerVisitor(this));
    }

    /**
     * Adds the FSA of a regex to this lexer and returns the
     * tokenIndex. If the regex already exists, the existing
     * tokenIndex can be returned. Theoretically this means
     * that this method can verify whether two regexes are
     * equivalent.
     *
     * @param newFSA The fsa to be added
     * @return the added token
     */
    private int add(FSA newFSA) {
        // fetch all accepting nodes of newFSA.
        // Set acceptor to RESERVATION in them.
        for (FSA.Node n : newFSA.accepting()) {
            n.acceptors.add(RESERVATION);
        }

        // merge
        fsa.or(newFSA);

        // Check whether there is already an int that is present in exactly all
        // accepting nodes in which also RESERVATION is found.
        // In this case, remove RESERVATION and return this int.
        // Otherwise fetch a new token from counter and replace RESERVATION by this token.

        // Create two sets: one for those that are in accepting states that are not reserved.
        IntSet outside = new IntSet();
        IntSet inside = null;

        ArrayList<FSA.Node> reservedNodes = new ArrayList<>();

        // XXX could be speeded up by removing outside from inside sooner.
        for (FSA.Node n : fsa.accepting()) {
            if (n.acceptors.contains(RESERVATION)) {
                reservedNodes.add(n);

                if (inside == null) {
                    inside = new IntSet();
                    inside.addAll(n.acceptors);
                } else {
                    inside.retainAll(n.acceptors);
                }
            } else {
                outside.addAll(n.acceptors);
            }
        }

        // there is at least one.
        assert inside != null;
        assert inside.contains(RESERVATION);
        assert !outside.contains(RESERVATION);

        inside.removeAll(outside);

        assert inside.size() <= 2;

        if (inside.size() == 1) {
            // we need a new token.
            int tokId = tokIdOffset.incr();
            for (FSA.Node n : reservedNodes) {
                n.acceptors.add(tokId);
                n.acceptors.remove(RESERVATION);
            }

            return tokId;
        } else {
            // we found a token already that is equivalent to this one.
            for (FSA.Node n : reservedNodes) {
                n.acceptors.remove(RESERVATION);
            }

            // return the larger token. (RESERVATION is negative.)
            return inside.last();
        }
    }

    /**
     * Adds a regular expression as a token.
     *
     * @param regex The regex to be added.
     * @return the index for this regex. If
     * there has already been a semantically
     * identical regex, an already existing
     * tokenIndex is returned.
     */
    public int add(Regex regex) {
        // XXX Check whether regex is empty to avoid infinite loops
        return add(regexToFsa(regex));
    }

    /**
     * Creates a new token from a text
     */
    public int add(String s) {
        return add(Regex.text(s));
    }

    /**
     * Fetches the next token from the token stream.
     * @param stream A frame stream in reset position, ie,
     *               either reset or advance should have
     *               been called prior to this call.
     * @return A set that should not be modified.
     */
    public IntSet readNextToken(FrameStream stream) {
        FSA.Node node = fsa.accept(stream);

        if (node == null) {
            return null;
        }

        return node.acceptors;
    }
}
