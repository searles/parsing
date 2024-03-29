package at.searles.parsing.ruleset

import at.searles.parsing.lexer.Lexer
import at.searles.parsing.lexer.TokenStream
import at.searles.parsing.lexer.regexp.CharSet
import at.searles.parsing.lexer.regexp.Text
import at.searles.parsing.parser.*
import at.searles.parsing.parser.Reducer.Companion.rep
import at.searles.parsing.parser.combinators.ref
import at.searles.parsing.parser.tools.InitValue
import org.junit.Assert
import org.junit.Test

class CycleCheck {
    @Test
    fun testSimpleCycleCausesInfiniteRecursion() {
        val rules = object: Grammar {
            override val lexer: Lexer = Lexer()

            val a: Parser<Int> by lazy { b }
            val b by lazy { text("a").init(1) or text("(") + a + text(")") }
        }
        try {
            rules.a.parse(TokenStream("a"))
            Assert.fail()
        } catch(e: StackOverflowError) {
            // e.printStackTrace()
        }
    }

    @Test
    fun testNoInfiniteRecursionOnRef() {
        val rules = object: Grammar {
            override val lexer: Lexer = Lexer()

            val a: Parser<Int> by ref { b }
            val b by lazy { text("a").init(1) or text("(") + a + text(")") }
        }

        val result = rules.a.parse(TokenStream("a"))
        Assert.assertTrue(result.isSuccess)
    }

    @Test
    fun testNoLongestMatchMissDueToLazyRefParsers() {
        val rules = object: Grammar {
            override val lexer: Lexer = Lexer()

            val mergeString = object: Fold<String, String, String> {
                override fun fold(left: String, right: String): String {
                    return "$left-$right"
                }
            }

            val emptyString = InitValue("")

            val id = rex(CharSet('a', 'z').rep1() - Text("excl"))

            val a: Parser<String> by ref { emptyString + (id + mergeString).rep(1) or b }
            val b by ref { text("excl").init("hello world") }
        }

        // since the excl-parser is only parsed afterwards and therefore
        // is not considered in the longest-match
        // the output is exc-l

        val result = rules.a.parse(TokenStream("excl"))
        Assert.assertTrue(result.isSuccess)
        Assert.assertEquals("hello world", result.value)
    }


    @Test
    fun testSelfLoop() {
        val rules = object: Grammar {
            override val lexer: Lexer = Lexer()
            val a: Parser<String> by ref { text("a") + a }
        }

        val result = rules.a.parse(TokenStream("a"))

        Assert.assertFalse(result.isSuccess)
    }
}