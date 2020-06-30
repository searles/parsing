package at.searles.lexer;

import at.searles.lexer.fsa.FSA;
import at.searles.lexer.utils.IntervalSet;
import at.searles.regexp.CharSet;
import at.searles.regexp.Regexp;
import at.searles.regexp.Visitor;
import org.jetbrains.annotations.NotNull;

/**
 * Visitor to create an fsa out of a regex.
 */
class LexerVisitor implements Visitor<FSA> {

    private final Lexer lexer;

    LexerVisitor(Lexer lexer) {
        this.lexer = lexer;
    }

    @Override
    public FSA visitOr(Regexp l, Regexp r) {
        FSA fsa = l.accept(this);
        return fsa.or(r.accept(this));
    }

    @Override
    public FSA visitThen(Regexp l, Regexp r) {
        FSA fsa = l.accept(this);
        return fsa.then(r.accept(this));
    }

    @Override
    public FSA visitFirstMatch(Regexp regexp) {
        return regexp.accept(this).shortest();
    }

    @Override
    public FSA visitOpt(Regexp regexp) {
        FSA parent = regexp.accept(this);
        return parent.rep1().opt();
    }

    @Override
    public FSA visitText(String string) {
        // leaf for all
        FSA fsa = null; // empty one.

        for (int i = 0; i < string.length(); ) {
            FSA nextFSA = new FSA(lexer.fsaNodeCounter, CharSet.Companion.chars(string.codePointAt(i)));
            if (fsa == null) {
                fsa = nextFSA;
            } else {
                fsa.then(nextFSA);
            }

            i += Character.charCount(string.codePointAt(i));
        }

        return fsa;
    }

//    @Override
//    public FSA visitRepRange(Regexp regexp, int min, int max) {
//        if (min < 0 || max < min) {
//            throw new IllegalArgumentException("bad ranges");
//        }
//
//        if (min == 0 && max == 0) {
//            return visitEmpty();
//        } else {
//            FSA fsa = null;
//
//            for (int i = 0; i < max; ++i) {
//                FSA nextFsa = regexp.accept(this);
//
//                if (i >= min) {
//                    nextFsa = nextFsa.opt();
//                }
//
//                fsa = i == 0 ? nextFsa : fsa.then(nextFsa);
//            }
//
//            return fsa;
//        }
//    }
//
//    @Override
//    public FSA visitRepCount(Regexp regexp, int count) {
//        if (count < 0) {
//            throw new IllegalArgumentException("count < 0!");
//        } else if (count == 0) {
//            return visitEmpty();
//        } else {
//            FSA fsa = null;
//
//            for (int i = 0; i < count; ++i) {
//                FSA nextFsa = regexp.accept(this);
//
//                fsa = i == 0 ? nextFsa : fsa.then(nextFsa);
//            }
//
//            return fsa;
//        }
//    }

    @Override
    public FSA visitAtLeast(Regexp regexp, int min) {
        if (min < 0) {
            throw new IllegalArgumentException("min < 0!");
        }

        FSA fsa = null;

        for (int i = 0; i < min; ++i) {
            FSA nextFsa = regexp.accept(this);

            fsa = i == 0 ? nextFsa.rep1().opt() : fsa.then(nextFsa);
        }

        return fsa;
    }

    @Override
    public FSA visitEmpty() {
        return new FSA(lexer.fsaNodeCounter, true);
    }

    @Override
    public FSA visitKleeneStar(@NotNull Regexp regexp) {
        return null;
    }

    @Override
    public FSA visitKleenePlus(@NotNull Regexp regexp) {
        return null;
    }

    @Override
    public FSA visitMinus(@NotNull Regexp l, @NotNull Regexp r) {
        return null;
    }

    @Override
    public FSA visitAnd(@NotNull Regexp l, @NotNull Regexp r) {
        return null;
    }

    @Override
    public FSA visitSet(@NotNull IntervalSet set) {
        return null;
    }

    @Override
    public FSA visitCount(@NotNull Regexp regexp, int count) {
        return null;
    }

    @Override
    public FSA visitRange(@NotNull Regexp regexp, int min, int max) {
        return null;
    }
}
