package at.searles.regex;

public interface Visitor<A> {

    A visitOr(Regex l, Regex r);

    A visitThen(Regex l, Regex r);

    A visitNonGreedy(Regex regex);

    A visitClosure(Regex regex, boolean reflexive, boolean transitive);

    /**
     * Use code points!
     */
    A visitText(String string);

    A visitRepRange(Regex regex, int min, int max);

    A visitRepCount(Regex regex, int count);

    A visitRepMin(Regex regex, int min);

    A visitEmpty();

    A visitCharSet(CharSet set);
}
