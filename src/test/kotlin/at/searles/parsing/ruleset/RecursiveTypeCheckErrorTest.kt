package at.searles.parsing.ruleset

import at.searles.parsing.lexer.Lexer
import at.searles.parsing.parser.Conversion
import at.searles.parsing.parser.FnResult
import at.searles.parsing.parser.Fold
import at.searles.parsing.parser.Parser
import at.searles.parsing.parser.Reducer.Companion.rep
import at.searles.parsing.parser.combinators.ref
import at.searles.parsing.parser.tools.cast

class RecursiveTypeCheckErrorTest {
    interface Expr {}
    
    interface Literal: Expr {}

    enum class Op {Plus, Minus, Times, Div, Power, Neg}

    class ExprParser: Grammar {
        override val lexer = Lexer()

        class CreateBinaryExpr(private val op: Op) :
            Fold<Expr, Expr, Expr> {
            override fun fold(left: Expr, right: Expr): Expr {
                TODO("Not yet implemented")
            }

            override fun invertLeft(value: Expr): FnResult<Expr> {
                TODO("Not yet implemented")
            }

            override fun invertRight(value: Expr): FnResult<Expr> {
                TODO("Not yet implemented")
            }
        }
        
        val arithmeticExpression by lazy<Parser<Expr>> {
            timesDiv + (
                    itext("+") + timesDiv + CreateBinaryExpr(Op.Plus) or
                    itext("-") + timesDiv + CreateBinaryExpr(Op.Minus)
            ).rep()
        }

        val timesDiv by lazy<Parser<Expr>> {
            power + (
                    itext("*") + power + CreateBinaryExpr(Op.Times) or
                    itext("/") + power + CreateBinaryExpr(Op.Div)
            ).rep()
        }

        val power by lazy<Parser<Expr>> {
            signed + (itext("**") + basis + CreateBinaryExpr(Op.Power)).rep()
        }
        
        class CreateUnaryExpr(private val op: Op) : Conversion<Expr, Expr> {
            override fun convert(value: Expr): Expr {
                TODO("Not yet implemented")
            }
        }
        
        val signed by lazy {
            itext("+") + basis or
            itext("-") + basis + CreateUnaryExpr(Op.Neg) or
            basis
        }

        val basis: Parser<Expr> by ref {
            literal + cast<Literal, Expr>() or
            itext("(") + arithmeticExpression + itext(")")
        }
        
        object Num: Literal 
        
        val literal by ref<Literal> {
            itext("0").init(Num)
        }
    }
}