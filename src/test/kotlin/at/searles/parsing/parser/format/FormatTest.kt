package at.searles.parsing.parser.format

import at.searles.parsing.lexer.Lexer
import at.searles.parsing.lexer.regexp.CharSet
import at.searles.parsing.parser.Parser
import at.searles.parsing.parser.Parser.Companion.asString
import at.searles.parsing.parser.ParserStream
import at.searles.parsing.parser.Recognizer
import at.searles.parsing.parser.RecognizerResult
import at.searles.parsing.parser.combinators.ref
import at.searles.parsing.parser.tools.NewInstance.of
import at.searles.parsing.parser.tools.cast
import at.searles.parsing.printer.PrintTree
import at.searles.parsing.printer.StringPrintTree
import at.searles.parsing.ruleset.Grammar
import org.junit.Assert
import org.junit.Test

class FormatTest {
    @Test
    fun testCanParseExpressions() {
        val result by Rules.expr.parse("f(1,2)")
        Assert.assertEquals(Call("f", listOf(Num("1"), Num("2"))), result)
    }

    @Test
    fun testCanPrintExpression() {
        val result = Rules.expr.print(Call("f", listOf(Num("1"), Num("2"))))
        Assert.assertEquals("f(1, 2)", result.asString())
    }

    @Test
    fun testCanParseEmptyBlocks() {
        val result by Rules.block.parse("{}")
        Assert.assertEquals(Block(emptyList()), result)
    }

    @Test
    fun testCanPrintEmptyBlocks() {
        val result = Rules.block.print(Block(emptyList()))
        Assert.assertEquals("{}", result.asString())
    }

    @Test
    fun testCanPrintBlocksWithItems() {
        val result = Rules.block.print(Block(listOf(Call("f", emptyList()))))
        Assert.assertEquals("{\n    f();\n}", result.asString())
    }

    interface Stmt
    data class Block(val stmts: List<Stmt>): Stmt
    data class IfStmt(val condition: Expr, val thenBranch: Stmt, val elseBranch: Stmt?): Stmt
    data class Assignment(val id: String, val rValue: Expr): Stmt
    interface Expr
    data class Call(val id: String, val args: List<Expr>): Stmt, Expr
    data class Num(val str: String): Expr

    object Rules: Grammar {

        override val lexer: Lexer = Lexer()

        val block: Parser<Block> by ref {
            text("{") + stmts + text("}") + of<Block>().create()
        }

        val stmts: Parser<List<Stmt>> by ref {
            (stmt + text(";")).rep()
        }

        val stmt: Parser<Stmt> by ref {
            ifstmt + cast<IfStmt, Stmt>() or
            block + cast() or
            assignment + cast() or
            call + cast()
        }

        val ifstmt: Parser<IfStmt> by ref {
            text("if") + expr + stmt + (text("else") + stmt).opt() + of<IfStmt>().create()
        }

        val assignment: Parser<Assignment> by ref {
            id + text("=") + expr + of<Assignment>().create()
        }

        object Separator: Recognizer {
            override fun parse(stream: ParserStream): RecognizerResult {
                return RecognizerResult.of(stream.index, 0)
            }

            override fun print(): PrintTree {
                return StringPrintTree(" ")
            }
        }

        val call: Parser<Call> by ref {
            id + text("(") + expr.join(text(",") + Separator) + text(")") + of<Call>().create()
        }

        val expr: Parser<Expr> by ref {
            call + cast<Call, Expr>() or
            num + cast()
        }

        val num: Parser<Num> by ref {
            rex("[0-9]+").asString() + of<Num>().create()
        }

        val id: Parser<String> by ref {
            rex(CharSet('a'..'z').rep1()).asString()
        }

        val ws = lexer.createSpecialToken(CharSet(' ', '\n', '\t', '\r').rep1())

//                    comment: '//' [^\n]* | ('/*' .* '*/')!
    }

//    1. Autoformat:
//    formatter.format("{ print(1); }")
//    --> Returns a list of commands like
//    Delete(index, length)
//    Insert(index, string)
//    Highlight(index, length, marker)
}