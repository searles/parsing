package at.searles.regexp;

public interface Visitor<A> {

    A visitOr(Regexp l, Regexp r);

    A visitThen(Regexp l, Regexp r);

    A visitNonGreedy(Regexp regexp);

    A visitClosure(Regexp regexp, boolean reflexive, boolean transitive);

    /**
     * Use code points!
     */
    A visitText(String string);

    A visitRepRange(Regexp regexp, int min, int max);

    A visitRepCount(Regexp regexp, int count);

    A visitRepMin(Regexp regexp, int min);

    A visitEmpty();

    A visitCharSet(CharSet set);
}
