package at.searles.parsing.parser.format

import at.searles.parsing.lexer.Lexer
import at.searles.parsing.parser.Parser
import at.searles.parsing.parser.Parser.Companion.asString
import at.searles.parsing.parser.Parser.Companion.orEmpty
import at.searles.parsing.parser.combinators.ref
import at.searles.parsing.parser.tools.NewInstance
import at.searles.parsing.parser.tools.Mark
import at.searles.parsing.parser.tools.cast
import at.searles.parsing.ruleset.Grammar
import at.searles.parsing.ruleset.RegexpGrammar

object Rules: Grammar {
    override val lexer: Lexer = Lexer()

    val block: Parser<Block> by ref {
        text("{") + (stmts.highlight("indent") + Mark("newLine")).orEmpty() + text("}") + NewInstance.of<Block>().create()
    }

    val stmts: Parser<List<Stmt>> by ref {
        (Mark("newLine") + (stmt + text(";") or block + cast())).rep(1)
    }

    val stmt: Parser<Stmt> by ref {
        ifstmt + cast<IfStmt, Stmt>() or
                assignment + cast() or
                call + cast()
    }

    val ifstmt: Parser<IfStmt> by ref {
        text("if") + expr + stmt + (text("else") + stmt).opt() + NewInstance.of<IfStmt>().create()
    }

    val assignment: Parser<Assignment> by ref {
        id + text("=") + expr + NewInstance.of<Assignment>().create()
    }

    val call: Parser<Call> by ref {
        id + text("(") + expr.join(text(",") + Mark("separator"))
            .orEmpty() + text(")") + NewInstance.of<Call>().create()
    }

    val expr: Parser<Expr> by ref {
        call + cast<Call, Expr>() or
                num.highlight("num") + cast()
    }

    val num: Parser<Num> by ref {
        rex("[0-9]+").asString() + NewInstance.of<Num>().create()
    }

    val id: Parser<String> by ref {
        rex("[a-z]+").asString()
    }

    val ws = lexer.createSpecialToken(RegexpGrammar.regexp.parse("""[\n\r\t ]+""").value)
//        val lineComment = lexer.createSpecialToken(RegexpGrammar.regexp.parse("""//[^\n]*""").value)
//        val comment = lexer.createSpecialToken(RegexpGrammar.regexp.parse("""(/\*.*\*/)!""").value)
}

interface Stmt
data class Block(val stmts: List<Stmt>): Stmt
data class IfStmt(val condition: Expr, val thenBranch: Stmt, val elseBranch: Stmt?): Stmt
data class Assignment(val id: String, val rValue: Expr): Stmt
interface Expr
data class Call(val id: String, val args: List<Expr>): Stmt, Expr
data class Num(val str: String): Expr