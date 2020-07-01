package at.searles.parsing.app

interface Expr {
    fun app(r: Expr): Expr {
        return App(this, r)
    }
}