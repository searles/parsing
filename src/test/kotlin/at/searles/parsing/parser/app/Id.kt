package at.searles.parsing.parser.app

import at.searles.parsing.parser.app.Expr

class Id(val id: String) : Expr {
    override fun toString(): String {
        return id
    }
}