package at.searles.parsing.parser.format

import at.searles.parsing.lexer.Lexer
import at.searles.parsing.lexer.regexp.Regexp
import at.searles.parsing.lexer.regexp.Text
import at.searles.parsing.parser.Conversion
import at.searles.parsing.parser.Parser
import at.searles.parsing.parser.Reducer.Companion.opt
import at.searles.parsing.parser.Reducer.Companion.rep
import at.searles.parsing.parser.combinators.TokenParser
import at.searles.parsing.parser.combinators.TokenRecognizer
import at.searles.parsing.parser.combinators.ref
import at.searles.parsing.parser.tools.CastBuilders.cast
import at.searles.parsing.parser.tools.CastBuilders.plus
import at.searles.parsing.parser.tools.reflection.NewInstanceBuilders.newInstance
import at.searles.parsing.parser.tools.reflection.NewInstanceBuilders.plus
import at.searles.parsing.ruleset.RegexpGrammar.regexp

@OptIn(ExperimentalStdlibApi::class)
class SimpleGrammar() {
    val lexer: Lexer = Lexer()
    
    val ws = regexp("[ \n]")
    val comment = regexp("//[^\n]*")

    val identifier = regexp("[a-zA-Z_][a-zA-Z0-9_]*")
    val number = regexp("[0-9]+")

    val stmt: Parser<Tree> by ref {
        ifstmt or
        whilestmt or
        block or
        assignment
    }

    val ifstmt: Parser<Tree> by ref {
        t("if") + expr + t("then") + stmt + (t("else") + stmt).opt() + newInstance<IfStmt>() + cast()
    }

    val whilestmt: Parser<Tree> by ref {
        t("while") + expr + t("do") + stmt + newInstance<WhileStmt>() + cast()
    }

    val block: Parser<Tree> by ref {
        t("{") + stmt.rep() + t("}") + newInstance<Block>() + cast()
    }

    val assignment: Parser<Tree> by ref {
        id + t("=") + expr + newInstance<Assignment>() + cast()
    }

    val expr: Parser<Tree> by ref {
        conjunction
    }

    val conjunction: Parser<Tree> by ref {
        disjunction + (
            t("&") + disjunction + newInstance<BiTree>(Op.And).left<Tree>() + cast<Tree>()
        ).rep()
    }

    val disjunction: Parser<Tree> by ref {
        literal + (
            t("|") + literal + newInstance<BiTree>(Op.Or).left<Tree>() + cast<Tree>()
        ).rep()
    }

    val literal: Parser<Tree> by ref {
        t("!") + literal + newInstance<UnTree>(Op.Not) + cast<Tree>() or
        cmp
    }

    val cmp: Parser<Tree> by ref {
        sum + (
            t(">") + sum + newInstance<BiTree>(Op.Greater).left<Tree>() + cast<Tree>() or
            t(">=") + sum + newInstance<BiTree>(Op.GreaterEquals).left<Tree>() + cast() or
            t("==") + sum + newInstance<BiTree>(Op.Equals).left<Tree>() + cast() or
            t("!=") + sum + newInstance<BiTree>(Op.NotEquals).left<Tree>() + cast() or
            t("<=") + sum + newInstance<BiTree>(Op.SmallerEquals).left<Tree>() + cast() or
            t("<") + sum + newInstance<BiTree>(Op.Smaller).left<Tree>() + cast()
        ).opt()
    }

    val sum: Parser<Tree> by ref {
        product + (
            t("+") + product + newInstance<BiTree>(Op.Plus).left<Tree>() + cast<Tree>() or
            t("-") + product + newInstance<BiTree>(Op.Minus).left<Tree>() + cast()
        ).rep()
    }

    val product: Parser<Tree> by ref {
        power + (
                t("*") + power + newInstance<BiTree>(Op.Times).left<Tree>() + cast<Tree>() or
                t("/") + power + newInstance<BiTree>(Op.Divide).left<Tree>() + cast() or
                t("%") + power + newInstance<BiTree>(Op.Modulo).left<Tree>() + cast()
        ).rep()
    }

    val power: Parser<Tree> by ref {
        negate + (
                t("^") + power + newInstance<BiTree>(Op.Pow).left<Tree>() + cast<Tree>()
        ).opt()
    }

    val negate: Parser<Tree> by ref {
        t("-") + negate + newInstance<UnTree>(Op.Neg) + cast<Tree>() or
        term
    }

    val term: Parser<Tree> by ref {
        app or num or t("(") + expr + t(")")
    }

    val app: Parser<Tree> by ref {
        id + (
            t("(") + expr.join(t(",")) + t(")") + newInstance<Application>().left<Tree>() + cast<Tree>()
        ).opt()
    }

    val id: Parser<Tree> = r(identifier) { it.toString() } + newInstance<Id>() + cast()

    val num: Parser<Tree> = r(number) { it.toString().toInt() } + newInstance<Num>() + cast()

    interface Tree
    data class IfStmt(val condition: Tree, val thenBranch: Tree, val elseBranch: Tree?): Tree
    data class WhileStmt(val condition: Tree, val body: Tree): Tree
    data class Block(val stmts: List<Tree>): Tree
    data class Assignment(val lValue: Tree, val rValue: Tree): Tree
    data class BiTree(val op: Op, val arg0: Tree, val arg1: Tree): Tree
    data class UnTree(val op: Op, val arg: Tree): Tree
    data class Application(val fn: Tree, val args: List<Tree>): Tree
    data class Id(val id: String): Tree
    data class Num(val num: Int): Tree
    
    private fun t(string: String): TokenRecognizer {
        return TokenRecognizer(lexer.createToken(Text(string)), lexer, string)
    }

    private fun <A> r(regexp: Regexp, conversion: Conversion<CharSequence, A>): TokenParser<A> {
        return TokenParser(lexer.createToken(regexp), lexer, conversion)
    }

    enum class Op {
        Not, Neg,
        Greater, GreaterEquals, Equals, NotEquals, SmallerEquals, Smaller,
        Plus, Minus, Times, Divide, Modulo, Pow,
        And, Or
    }
}