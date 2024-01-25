package at.searles.parsing.parser.format.simple

import at.searles.parsing.lexer.Lexer
import at.searles.parsing.lexer.TokenStream
import at.searles.parsing.lexer.regexp.Regexp
import at.searles.parsing.lexer.regexp.Text
import at.searles.parsing.parser.*
import at.searles.parsing.parser.Reducer.Companion.opt
import at.searles.parsing.parser.Reducer.Companion.rep
import at.searles.parsing.parser.combinators.TokenParser
import at.searles.parsing.parser.combinators.TokenRecognizer
import at.searles.parsing.parser.combinators.ref
import at.searles.parsing.parser.tools.CastBuilders.cast
import at.searles.parsing.parser.tools.CastBuilders.plus
import at.searles.parsing.parser.tools.reflection.NewInstanceBuilders.newInstance
import at.searles.parsing.parser.tools.reflection.NewInstanceBuilders.plus
import at.searles.parsing.printer.PrintTree
import at.searles.parsing.ruleset.RegexpGrammar.regexp
import at.searles.parsing.parser.format.simple.Tree.*

@OptIn(ExperimentalStdlibApi::class)
class SimpleGrammar {
    val lexer: Lexer = Lexer()

    val spaces = regexp("[ ]+")
    val whiteSpaces = regexp("[ \n]+")
    val comment = regexp("//[^\n]*")

    val identifier = regexp("[a-zA-Z_][a-zA-Z0-9_]*")
    val number = regexp("[0-9]+")

    val stmt: Parser<Tree> by ref {
        ifstmt or
        whilestmt or
        block or
        assignment or
        expr
    }

    val program: Parser<Tree> by ref {
        stmt.rep() + newInstance<Block>() + cast() // TODO: How to New-Line after each stmt?
    }

    val ifstmt: Parser<Tree> by ref {
        t("if", Marking.Keyword) + expr + t("then", Marking.Keyword) + stmt + (t("else", Marking.Keyword) + stmt).opt() + newInstance<IfStmt>() + cast()
    }

    val whilestmt: Parser<Tree> by ref {
        t("while", Marking.Keyword) + expr + t("do", Marking.Keyword) + stmt + newInstance<WhileStmt>() + cast()
    }

    val block: Parser<Tree> by ref {
        t("{", Marking.OpenNl) + stmt.rep() + t("}", Marking.CloseNl) + newInstance<Block>() + cast()
    }

    val assignment: Parser<Tree> by ref {
        id + t("=", Marking.Binary) + expr + newInstance<Assignment>() + cast()
    }

    val expr: Parser<Tree> by ref {
        conjunction
    }

    val conjunction: Parser<Tree> by ref {
        disjunction + (
            t("&", Marking.Binary) + disjunction + newInstance<BiTree>(Op.And).left<Tree>() + cast<Tree>()
        ).rep()
    }

    val disjunction: Parser<Tree> by ref {
        literal + (
            t("|", Marking.Binary) + literal + newInstance<BiTree>(Op.Or).left<Tree>() + cast<Tree>()
        ).rep()
    }

    val literal: Parser<Tree> by ref {
        t("!", Marking.Unary) + literal + newInstance<UnTree>(Op.Not) + cast<Tree>() or
        cmp
    }

    val cmp: Parser<Tree> by ref {
        sum + (
            t(">", Marking.Binary) + sum + newInstance<BiTree>(Op.Greater).left<Tree>() + cast<Tree>() or
            t(">=", Marking.Binary) + sum + newInstance<BiTree>(Op.GreaterEquals).left<Tree>() + cast() or
            t("==", Marking.Binary) + sum + newInstance<BiTree>(Op.Equals).left<Tree>() + cast() or
            t("!=", Marking.Binary) + sum + newInstance<BiTree>(Op.NotEquals).left<Tree>() + cast() or
            t("<=", Marking.Binary) + sum + newInstance<BiTree>(Op.SmallerEquals).left<Tree>() + cast() or
            t("<", Marking.Binary) + sum + newInstance<BiTree>(Op.Smaller).left<Tree>() + cast()
        ).opt()
    }

    val sum: Parser<Tree> by ref {
        product + (
            t("+", Marking.Binary) + product + newInstance<BiTree>(Op.Plus).left<Tree>() + cast<Tree>() or
            t("-", Marking.Binary) + product + newInstance<BiTree>(Op.Minus).left<Tree>() + cast()
        ).rep()
    }

    val product: Parser<Tree> by ref {
        power + (
                t("*", Marking.Binary) + power + newInstance<BiTree>(Op.Times).left<Tree>() + cast<Tree>() or
                t("/", Marking.Binary) + power + newInstance<BiTree>(Op.Divide).left<Tree>() + cast() or
                t("%", Marking.Binary) + power + newInstance<BiTree>(Op.Modulo).left<Tree>() + cast()
        ).rep()
    }

    val power: Parser<Tree> by ref {
        negate + (
                t("^", Marking.Binary) + power + newInstance<BiTree>(Op.Pow).left<Tree>() + cast<Tree>()
        ).opt()
    }

    val negate: Parser<Tree> by ref {
        t("-", Marking.Unary) + negate + newInstance<UnTree>(Op.Neg) + cast<Tree>() or
        term
    }

    val term: Parser<Tree> by ref {
        app or num or t("(", Marking.Open) + expr + t(")", Marking.Close)
    }

    val app: Parser<Tree> by ref {
        id + (
            t("(", Marking.Open) + expr.join(t(",", Marking.Separator)) + t(")", Marking.Close) + newInstance<Application>().left<Tree>() + cast<Tree>()
        ).opt()
    }

    val id: Parser<Tree> = r(identifier, Marking.Name) { it.toString() } + newInstance<Id>() + cast()

    val num: Parser<Tree> = r(number, Marking.Name) { it.toString().toInt() } + newInstance<Num>() + cast()

    private fun t(string: String, mark: Marking = Marking.None): Recognizer {
        return MyTokenRecognizer(TokenRecognizer(lexer.createToken(Text(string)), lexer, string))
    }

    private fun <A> r(regexp: Regexp, marking: Marking = Marking.None, conversion: Conversion<CharSequence, A>): Parser<A> {
        return MyTokenParser(TokenParser(lexer.createToken(regexp), lexer, conversion))
    }

    val spacesToken = lexer.createToken(spaces)
    val writeSpacesToken = lexer.createToken(whiteSpaces)
    val commentToken = lexer.createToken(comment)

    enum class Marking {
        Separator, Open, Close, OpenNl, CloseNl, Unary, Binary, Name, Keyword, None
    }

    fun consumeHiddenPreTokens(stream: TokenStream) {
        var nextTokens = stream.getTokenIds(lexer)

        while(nextTokens != null && (nextTokens.contains(writeSpacesToken) || nextTokens.contains(commentToken))) {
            stream.next()
            nextTokens = stream.getTokenIds(lexer)
        }
        // TODO: When formatting, delete all white chars and insert according to marks
    }

    fun consumeHiddenPostTokens(stream: TokenStream) {
        var nextTokens = stream.getTokenIds(lexer)

        while(nextTokens != null && (nextTokens.contains(spacesToken) || nextTokens.contains(commentToken))) {
            stream.next()
            nextTokens = stream.getTokenIds(lexer)
        }
    }

    inner class MyTokenParser<A>(val wrapped: TokenParser<A>): Parser<A> {
        override fun parse(stream: TokenStream): ParserResult<A> {
            consumeHiddenPreTokens(stream)
            val result = wrapped.parse(stream)

            if(result.isSuccess) {
                consumeHiddenPostTokens(stream)
            }

            return result
        }

        override fun print(value: A): PrintTree {
            return wrapped.print(value)
        }
    }

    inner class MyTokenRecognizer(val wrapped: TokenRecognizer): Recognizer {
        override fun parse(stream: TokenStream): RecognizerResult {
            consumeHiddenPreTokens(stream)
            val result = wrapped.parse(stream)

            if(result.isSuccess) {
                consumeHiddenPostTokens(stream)
            }

            return result
        }

        override fun print(): PrintTree {
            return super.print()
        }
    }

    /*
    Formatting:

     */
}