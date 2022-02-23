package at.searles.parsing.printer

import at.searles.parsing.lexer.Lexer
import at.searles.parsing.lexer.TokenStream
import at.searles.parsing.lexer.regexp.CharSet
import at.searles.parsing.lexer.regexp.Text
import at.searles.parsing.parser.*
import at.searles.parsing.parser.Reducer.Companion.rep
import at.searles.parsing.parser.combinators.RefParser
import at.searles.parsing.parser.combinators.TokenParser
import at.searles.parsing.parser.combinators.TokenRecognizer
import at.searles.parsing.parser.combinators.ref
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
        override fun convert(value: CharSequence): Tree {
            return Term(value.toString())
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

    val lexer = Lexer()

    val ws = TokenRecognizer(lexer.createToken(Text(" ")), lexer, " ")

    val term by ref { TokenParser(lexer.createToken(CharSet('a' .. 'z').rep1()), lexer, TermCreate) }
    
    val app: Parser<Tree> by ref { expr + (ws + expr + AppCreate).rep() }
    
    var expr = RefParser("expr") {
        term or TokenRecognizer.text("(", lexer) + app + TokenRecognizer.text(")", lexer)
    }

    var indent: Int = 0

    @Before
    fun resetIndentation() {
        indent = 0
    }

    @Test
    fun testSimpleApp() {
        val tree = app.parse(TokenStream("a b"))

        Assert.assertTrue(tree.isSuccess)
        Assert.assertEquals("(a b)", tree.value.toString())
    }

    @Test
    fun testPrintNestedApp() {
        val tree = app.parse(TokenStream("(a b) c d (e f)"))
        val printTree = app.print(tree.value)

        Assert.assertTrue(printTree.isSuccess)
        Assert.assertEquals("a b c d (e f)", printTree.toString())
    }

    @Test
    fun testNestedApp() {
        val tree = app.parse(TokenStream("(a b) c d (e f)"))

        Assert.assertTrue(tree.isSuccess)
        Assert.assertEquals("((((a b) c) d) (e f))", tree.value.toString())
    }
}

