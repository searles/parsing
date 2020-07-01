package at.searles.parsing.app

class Id(val id: String) : Expr {
    override fun toString(): String {
        return id
    }
}