package at.searles.parsing.printer

import at.searles.parsing.lexer.Lexer
import at.searles.parsing.lexer.regexp.CharSet
import at.searles.parsing.parser.*
import at.searles.parsing.parser.Reducer.Companion.rep
import at.searles.parsing.parser.combinators.LazyParser
import at.searles.parsing.parser.combinators.TokenParser
import at.searles.parsing.parser.combinators.TokenRecognizer
import org.junit.Assert
import org.junit.Test

class IndentTest {
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

    object TermCreate: Conversion<String, Tree> {
        override fun convert(left: String): Tree {
            return Term(left)
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
        createSpecialToken(CharSet.chars(' ', '\n'))
    }

    val term = TokenParser(lexer.createToken(CharSet.interval('a' .. 'z').rep1())) + TermCreate

    val exprRef: Parser<Tree> = LazyParser { expr }

    val app = exprRef + (exprRef + AppCreate).rep()

    val expr =
        term or
        TokenRecognizer.text("(", lexer) + app + TokenRecognizer.text(")", lexer)

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
        Assert.assertEquals("abcd(ef)", printing.output)
    }


    @Test
    fun testNestedApp() {
        val tree = app.parse(ParserStream("(a b) c d (e f)"))

        Assert.assertTrue(tree.isSuccess)
        Assert.assertEquals("((((a b) c) d) (e f))", tree.value.toString())
    }
}

