package at.searles.parsing.parser.app

class App(val left: Expr, val right: Expr) : Expr {
    override fun toString(): String {
        return String.format("A(%s, %s)", left, right)
    }

}