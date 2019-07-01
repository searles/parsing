package at.searles.lexer;

import at.searles.lexer.fsa.FSA;
import at.searles.regex.CharSet;
import at.searles.regex.Regex;
import at.searles.regex.Visitor;

/**
 * Visitor to create an fsa out of a regex.
 */
class LexerVisitor implements Visitor<FSA> {

    private final Lexer lexer;

    LexerVisitor(Lexer lexer) {
        this.lexer = lexer;
    }

    @Override
    public FSA visitOr(Regex l, Regex r) {
        FSA fsa = l.accept(this);
        return fsa.or(r.accept(this));
    }

    @Override
    public FSA visitThen(Regex l, Regex r) {
        FSA fsa = l.accept(this);
        return fsa.then(r.accept(this));
    }

    @Override
    public FSA visitNonGreedy(Regex regex) {
        return regex.accept(this).shortest();
    }

    @Override
    public FSA visitClosure(Regex regex, boolean reflexive, boolean transitive) {
        FSA parent = regex.accept(this);
        if(transitive) {
            parent.plus();
        }

        if(reflexive) {
            parent.opt();
        }

        return parent;
    }

    @Override
    public FSA visitText(String string) {
        // leaf for all
        FSA fsa = null; // empty one.

        for (int i = 0; i < string.length();) {
            FSA nextFSA = new FSA(lexer.fsaNodeCounter, CharSet.chars(string.codePointAt(i)));
            if (fsa == null) {
                fsa = nextFSA;
            } else {
                fsa.then(nextFSA);
            }

            i += Character.charCount(string.codePointAt(i));
        }

        return fsa;
    }

    @Override
    public FSA visitRepRange(Regex regex, int min, int max) {
        if(min < 0 || max < min) {
            throw new IllegalArgumentException("bad ranges");
        }

        if(min == 0 && max == 0) {
            return visitEmpty();
        } else {
            FSA fsa = null;

            for(int i = 0; i < max; ++i) {
                FSA nextFsa = regex.accept(this);

                if(i >= min) {
                    nextFsa = nextFsa.opt();
                }

                fsa = i == 0 ? nextFsa : fsa.then(nextFsa);
            }

            return fsa;
        }
    }

    @Override
    public FSA visitRepCount(Regex regex, int count) {
        if(count < 0) {
            throw new IllegalArgumentException("count < 0!");
        } else if(count == 0) {
            return visitEmpty();
        } else {
            FSA fsa = null;

            for(int i = 0; i < count; ++i) {
                FSA nextFsa = regex.accept(this);

                fsa = i == 0 ? nextFsa : fsa.then(nextFsa);
            }

            return fsa;
        }
    }

    @Override
    public FSA visitRepMin(Regex regex, int min) {
        if(min < 0) {
            throw new IllegalArgumentException("min < 0!");
        }

        FSA fsa = null;

        for(int i = 0; i < min; ++i) {
            FSA nextFsa = regex.accept(this);

            fsa = i == 0 ? nextFsa.plus().opt() : fsa.then(nextFsa);
        }

        return fsa;
    }

    @Override
    public FSA visitEmpty() {
        return new FSA(lexer.fsaNodeCounter, true);
    }

    @Override
    public FSA visitCharSet(CharSet set) {
        return new FSA(lexer.fsaNodeCounter, set);
    }
}
