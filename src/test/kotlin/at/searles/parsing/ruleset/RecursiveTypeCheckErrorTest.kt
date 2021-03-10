package at.searles.parsing.ruleset

import at.searles.parsing.lexer.Lexer
import at.searles.parsing.parser.Parser
import at.searles.parsing.parser.Reducer.Companion.rep
import at.searles.parsing.parser.combinators.ref
import at.searles.parsing.parser.tools.CastBuilders.cast
import at.searles.parsing.parser.tools.CastBuilders.plus
import at.searles.parsing.parser.tools.NewInstanceBuilders.newInstance
import at.searles.parsing.parser.tools.NewInstanceBuilders.plus
import org.junit.Assert
import org.junit.Test

class RecursiveTypeCheckErrorTest {
    @ExperimentalStdlibApi
    @Test
    fun testParseExpr() {
        val src = "(2+42)*1/-33-3^(2-+4)"

        val result = ExprGrammar.expr.parse(src)

        Assert.assertTrue(result.isSuccess)
    }

    @ExperimentalStdlibApi
    @Test
    fun testPrintExpr() {
        val src = "(2+42)*1/-33-3^(2-4)"

        val expr = ExprGrammar.expr.parse(src).value

        Assert.assertEquals(src, ExprGrammar.expr.print(expr).asString())
    }

    interface Expr {}
    
    interface Literal: Expr {}

    class Num(val num: Int): Literal

    enum class Op {Plus, Minus, Times, Div, Power, Neg}

    data class BinExpr(val op: Op, val arg0: Expr, val arg1: Expr): Expr
    data class UnExpr(val op: Op, val arg: Expr): Expr

    @ExperimentalStdlibApi
    object ExprGrammar: Grammar {
        override val lexer = Lexer()
        
        val expr by ref {
            timesDiv + (
                    itext("+") + timesDiv + newInstance<BinExpr>(Op.Plus).left<Expr>() + cast<Expr>() or
                    itext("-") + timesDiv + newInstance<BinExpr>(Op.Minus).left<Expr>() + cast()
            ).rep()
        }

        val timesDiv by ref {
            power + (
                    itext("*") + power + newInstance<BinExpr>(Op.Times).left<Expr>() + cast<Expr>() or
                    itext("/") + power + newInstance<BinExpr>(Op.Div).left<Expr>() + cast()
            ).rep()
        }

        val power: Parser<Expr> by ref {
            signed + (itext("^") + basis + newInstance<BinExpr>(Op.Power).left<Expr>() + cast<Expr>()).rep()
        }
        
        val signed: Parser<Expr> by ref {
            itext("-") + basis + newInstance<UnExpr>(Op.Neg) + cast<Expr>() or
            itext("+").opt() + basis
        }

        val basis: Parser<Expr> by ref {
            literal + cast<Expr>() or
            itext("(") + expr + itext(")")
        }

        val literal by ref<Literal> {
            rex("[0-9]+") { it.toString().toInt() } + newInstance<Num>() + cast()
        }
    }
}