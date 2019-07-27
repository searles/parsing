package at.searles.parsing.app.test;


public interface Expr {
    default Expr app(Expr r) {
        return new App(this, r);
    }

    default Expr left() {
        return null;
    }

    default Expr right() {
        return null;
    }

    default String id() {
        return null;
    }
}