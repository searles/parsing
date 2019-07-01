package at.searles.lexer.utils;

import java.util.Iterator;
import java.util.TreeSet;

/**
 * This is a simple extension of a TreeSet that adds lexical ordering. Thus,
 * it can be used as a key for TreeMaps/TreeSets.
 * @param <A>
 */
public class LexicalSet<A extends Comparable<A>> implements Comparable<LexicalSet<A>>, Iterable<A> {

    public static <A extends Comparable<A>> LexicalSet<A> create(A...as) {
        LexicalSet<A> set = new LexicalSet<>();

        for(A a : as) {
            set.add(a);
        }

        return set;
    }

    private final TreeSet<A> set;

    public LexicalSet() {
        this.set = new TreeSet<A>();
    }

    public int size() {
        return set.size();
    }

    public A first() {
        return set.first();
    }

    public A last() {
        return set.last();
    }

    public LexicalSet<A> add(A a) {
        set.add(a);
        return this;
    }

    public LexicalSet<A> addAll(Iterable<A> as) {
        for(A a : as) {
            add(a);
        }

        return this;
    }

    public LexicalSet<A> addAll(A...as) {
        for(A a : as) {
            add(a);
        }

        return this;
    }

    public boolean contains(A a) {
        return set.contains(a);
    }

    public Iterator<A> iterator() {
        return set.iterator();
    }

    @Override
    public String toString() {
        return set.toString();
    }

    @Override
    public int compareTo(LexicalSet<A> that) {
        // lexical order
        Iterator<A> i0 = this.iterator();
        Iterator<A> i1 = that.iterator();

        while(true) {
            if(i0.hasNext()) {
                if(i1.hasNext()) {
                    A a0 = i0.next();
                    A a1 = i1.next();

                    int cmp = a0.compareTo(a1);

                    if(cmp != 0) return cmp;
                    // case cmp = 0: goto next elements.
                } else {
                    // that is shorter
                    return 1;
                }
            } else if(i1.hasNext()) {
                // this is shorter
                return -1;
            } else {
                // no new elements
                break;
            }
        }
        // no new elements
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        return o != null && (this == o || (getClass() == o.getClass() && compareTo((LexicalSet<A>) o) == 0));
    }
}