package at.searles.utils;

import java.util.Objects;

public class Triple<L, M, R> implements Comparable<Triple<L, M, R>> {
	private final L l;
	private final M m;
	private final R r;

	public Triple(L l, M m, R r) {
		// if(a == null || b == null) throw new NullPointerException();

		this.l = l;
		this.m = m;
		this.r = r;
	}

	public String toString() {
		return String.format("(%s, %s, %s)", l, m, r);
	}

	public L l() {
		return l;
	}

    public M m() {
        return m;
    }

    public R r() {
		return r;
	}

	@Override
	public int compareTo(Triple<L, M, R> that) {
        Comparable<L> l0 = (Comparable<L>) this.l;
        Comparable<M> m0 = (Comparable<M>) this.m;
        Comparable<R> r0 = (Comparable<R>) this.r;

        int cmp = l0.compareTo(that.l);

        if(cmp != 0) {
            return cmp;
        }

        cmp = m0.compareTo(that.m);

        if(cmp != 0) {
            return cmp;
        }

        return r0.compareTo(that.r);
    }

	@Override
	public int hashCode() {
        return (Objects.hashCode(l) * 31 + Objects.hashCode(m)) * 31 + Objects.hashCode(r);
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) return true;
        if (that == null) return false;
        return this.getClass() == that.getClass() &&
                Objects.equals(this.l, ((Triple<?, ?, ?>) that).l) &&
                Objects.equals(this.m, ((Triple<?, ?, ?>) that).m) &&
                Objects.equals(this.r, ((Triple<?, ?, ?>) that).r);
    }
}
