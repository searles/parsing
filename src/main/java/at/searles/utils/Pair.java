package at.searles.utils;

import java.util.Objects;

public class Pair<L, R> implements Comparable<Pair<L, R>> {
	private final L l;
	private final R r;

	public Pair(L l, R r) {
		// if(a == null || b == null) throw new NullPointerException();

		this.l = l;
		this.r = r;
	}

	public String toString() {
		return "(" + l + ", " + r + ")";
	}

	public L l() {
		return l;
	}

	public R r() {
		return r;
	}

	@Override
	public int compareTo(Pair<L, R> that) {
        Comparable<L> l0 = (Comparable<L>) this.l;
        Comparable<R> r0 = (Comparable<R>) this.r;

        int cmp = l0.compareTo(that.l);

        return cmp != 0 ? cmp : r0.compareTo(that.r);
	}

	@Override
	public int hashCode() {
        return Objects.hashCode(l) * 31 + Objects.hashCode(r);
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) return true;
        if (that == null) return false;
        return this.getClass() == that.getClass() &&
                Objects.equals(this.l, ((Pair<?, ?>) that).l) &&
                Objects.equals(this.r, ((Pair<?, ?>) that).r);
    }
}
