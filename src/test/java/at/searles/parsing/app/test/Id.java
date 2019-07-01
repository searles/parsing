package at.searles.parsing.app.test;

public class Id implements Expr {
	public final String id;
	
	public Id(String id) {
		this.id = id;
	}

	public String toString() {
		return id;
	}

	public String id() {
		return id;
	}
}