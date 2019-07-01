package at.searles.parsing.app.test;

public class App implements Expr {
	public final Expr l;
	public final Expr r;
	
	public App(Expr l, Expr r) {
		this.l = l;
		this.r = r;
	}

	@Override
	public Expr left() {
		return l;
	}

	@Override
	public Expr right() {
		return r;
	}
	
	public String toString() {
		return String.format("A(%s, %s)", l, r);
	}

}