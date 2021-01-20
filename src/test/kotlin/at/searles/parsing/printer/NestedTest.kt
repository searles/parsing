package at.searles.parsing.printer

import at.searles.parsing.lexer.Lexer
import at.searles.parsing.lexer.regexp.CharSet
import at.searles.parsing.parser.*
import at.searles.parsing.parser.Reducer.Companion.rep
import at.searles.parsing.parser.combinators.LazyParser
import at.searles.parsing.parser.combinators.TokenParser
import at.searles.parsing.parser.combinators.TokenRecognizer
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class NestedTest {
    interface Tree {}

    class Term(val id: String): Tree {
        override fun toString(): String {
            return id
        }
    }

    class App(val left: Tree, val right: Tree): Tree {
        override fun toString(): String {
            return "($left $right)"
        }
    }

    object TermCreate: Conversion<CharSequence, Tree> {
        override fun convert(left: CharSequence): Tree {
            return Term(left.toString())
        }

        override fun invert(value: Tree): FnResult<String> {
            return FnResult.ofNullable((value as? Term)?.id)
        }
    }

    object AppCreate: Fold<Tree, Tree, Tree> {
        override fun fold(left: Tree, right: Tree): Tree {
            return App(left, right)
        }

        override fun invertLeft(value: Tree): FnResult<Tree> {
            return FnResult.ofNullable((value as? App)?.left)
        }

        override fun invertRight(value: Tree): FnResult<Tree> {
            return FnResult.ofNullable((value as? App)?.right)
        }
    }

    val lexer = Lexer().apply {
        createSpecialToken(CharSet(' ', '\n'))
    }

    val term = TokenParser(lexer.createToken(CharSet('a' .. 'z').rep1())) + TermCreate

    val expr = LazyParser<Tree>()

    val app = expr + (expr + AppCreate).rep()

    @Before
    fun setUp() {
        expr.parser = term or
               TokenRecognizer.text("(", lexer) + app + TokenRecognizer.text(")", lexer)
    }

    @Test
    fun testSimpleApp() {
        val tree = app.parse(ParserStream("a b"))

        Assert.assertTrue(tree.isSuccess)
        Assert.assertEquals("(a b)", tree.value.toString())
    }

    @Test
    fun testPrintNestedApp() {
        val tree = app.parse(ParserStream("(a b) c d (e f)"))
        val printing = app.print(tree.value)

        Assert.assertTrue(printing.isSuccess)
        Assert.assertEquals("abcd(ef)", printing.output.asString())
    }


    @Test
    fun testNestedApp() {
        val tree = app.parse(ParserStream("(a b) c d (e f)"))

        Assert.assertTrue(tree.isSuccess)
        Assert.assertEquals("((((a b) c) d) (e f))", tree.value.toString())
    }

    var indent: Int = 0

    @Before
    fun resetIndentation() {
        indent = 0
    }

    class IndentationContext {
        var level = 0

        val indent = PrintInject {
            it.append("\n")
            level++
            it.append(" ".repeat(level))
        }


        val unindent = PrintInject {
            it.append("\n")
            level--
            it.append(" ".repeat(level))
        }
    }

    @Test
    fun testIndentation() {
        val ctx = IndentationContext()

        expr.parser = term or
                TokenRecognizer.text("(", lexer) + ctx.indent + app + ctx.unindent + TokenRecognizer.text(")", lexer)

        val tree = app.parse(ParserStream("(a (b (c d e) f) g) h"))
        val printTree = app.print(tree.value)

        val os = StringOutStream()
        printTree.output.print(os)

        Assert.assertEquals("a(\n" +
                " b(\n" +
                "  cde\n" +
                " )f\n" +
                ")gh", os.toString())
    }

    // TODO further tests with backtracking
}

