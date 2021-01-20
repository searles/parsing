package at.searles.parsing.parser.app

interface Expr {
    fun app(r: Expr): Expr {
        return App(this, r)
    }
}