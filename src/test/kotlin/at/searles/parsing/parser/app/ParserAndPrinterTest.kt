package at.searles.parsing.parser.app

import at.searles.parsing.codepoint.CodePointStream
import at.searles.parsing.lexer.Lexer
import at.searles.parsing.lexer.regexp.CharSet
import at.searles.parsing.parser.*
import at.searles.parsing.parser.Reducer.Companion.opt
import at.searles.parsing.parser.Reducer.Companion.rep
import at.searles.parsing.parser.combinators.LazyParser
import at.searles.parsing.parser.combinators.TokenParser
import at.searles.parsing.parser.combinators.TokenRecognizer
import at.searles.parsing.printer.PrintTree
import org.junit.Assert
import org.junit.Test
import java.util.*
import kotlin.math.abs


class ParserAndPrinterTest {
    private lateinit var parser: Parser<Expr>
    private lateinit var input: ParserStream
    private lateinit var value: Expr
    private lateinit var output: PrintTree

    @Test
    fun testIdIterativeParser() {
        // prepare
        withParser(iterativeParser())
        withInput("a")

        // act
        parse()

        // test
        Assert.assertEquals("a", value.toString())
    }

    @Test
    fun testIdRecursiveParser() {
        withParser(recursiveParser())
        withInput("a")
        parse()
        Assert.assertEquals("a", value.toString())
    }

    @Test
    fun testIdWrappedIterativeParser() {
        withParser(iterativeParser())
        withInput("((a))")
        parse()
        Assert.assertEquals("a", value.toString())
    }

    @Test
    fun testIdWrappedRecursiveParser() {
        withParser(recursiveParser())
        withInput("((a))")
        parse()
        Assert.assertEquals("a", value.toString())
    }

    @Test
    fun testSimpleAppIterativeParser() {
        withParser(iterativeParser())
        withInput("ab")
        parse()
        print()
        Assert.assertEquals("ab", output.toString())
    }

    @Test
    fun testSimpleAppRecursiveParser() {
        withParser(recursiveParser())
        withInput("ab")
        parse()
        print()
        Assert.assertEquals("ab", output.toString())
    }

    @Test
    fun testLongAppIterativeParser() {
        withParser(iterativeParser())
        withInput("abc(def)")
        parse()
        print()
        Assert.assertEquals("abc(def)", output.toString())
    }

    @Test
    fun testLongAppRecursiveParser() {
        withParser(recursiveParser())
        withInput("abc(def)")
        parse()
        print()
        Assert.assertEquals("abcdef", output.toString())
    }

    @Test
    fun testIterativeParserRecursivePrinter() {
        withParser(iterativeParser())
        withInput("abcde")
        parse()
        withParser(recursiveParser())
        print()
        Assert.assertEquals("(((ab)c)d)e", output.toString())
    }

    @Test
    fun testRecursiveParserIterativePrinter() {
        withParser(recursiveParser())
        withInput("abcde")
        parse()
        withParser(iterativeParser())
        print()
        Assert.assertEquals("a(b(c(de)))", output.toString())
    }

    @Test
    fun testLotsOfData() {
        // about 3.5 seconds for 1000000 (on big one 0.5)
        // about 35 seconds for 10000000 (on big one 17 seconds)
        input = ParserStream(stream(3000000))

        val startTime = System.currentTimeMillis()
        val duration = { (System.currentTimeMillis() - startTime).toFloat() / 1000f }

        withParser(iterativeParser())
        parse()
        println("Parser successful: ${duration()}")
        print()
        println("Printer successful: ${duration()}")
        val str = output.toString()
        println(str.length)
        withInput(str)
        parse()
        println("Parsing output successful: ${duration()}")
        print()
        val str2 = output.toString()
        Assert.assertEquals(str, str2)
        println("duration: ${duration()}")
        //System.out.println(this.output);
    }

    private fun stream(sizeLimit: Int): CodePointStream {
        return object : CodePointStream {
            val rnd = Random()
            var countOpen = 0
            var count = 0
            var justOpened = true
            override fun read(): Int {
                var random = abs(rnd.nextInt())
                if (count > sizeLimit && !justOpened) {
                    if (countOpen > 0) {
                        countOpen--
                        return ')'.toInt()
                    }
                    return -1
                }
                count++
                random = if (count > sizeLimit) random % 26 else random % 40
                if (random < 26) {
                    justOpened = false
                    return random + 'a'.toInt()
                }
                if (countOpen > 0 && random % 3 != 0 && !justOpened) {
                    countOpen--
                    return ')'.toInt()
                }
                justOpened = true
                countOpen++
                return '('.toInt()
            }
        }
    }

    private fun withInput(input: String) {
        this.input = ParserStream(input)
    }

    private fun parse() {
        val result = parser.parse(input)
        Assert.assertTrue(result.isSuccess)
        value = result.value
    }

    private fun print() {
        output = parser.print(value)
        Assert.assertTrue(output.isSuccess)
    }

    private fun withParser(parser: Parser<Expr>) {
        this.parser = parser
    }
    
    private fun recursiveParser(): Parser<Expr> {
        val exprParser = LazyParser<Expr>()
        val term = term(exprParser)
        val exprReducer: Reducer<Expr, Expr> = appReducer(exprParser)
        
        exprParser.parser = term + exprReducer.opt()
        
        return exprParser
    }
    
    private fun iterativeParser(): Parser<Expr> {
        val exprParser = LazyParser<Expr>()
        val term = term(exprParser)
        val appReducer: Reducer<Expr, Expr> = appReducer(term)

        exprParser.parser = term + appReducer.rep()

        return exprParser
    }

    private val idToExpr = object: Conversion<CharSequence, Expr> {
        override fun convert(value: CharSequence): Expr {
            return Id(value.toString())
        }

        override fun invert(value: Expr): FnResult<CharSequence> {
            return FnResult.ofNullable((value as? Id)?.id)
        }
    }

    private fun term(exprParser: Parser<Expr>): Parser<Expr> {
        val tokenizer = Lexer()

        val idToken = tokenizer.createToken(CharSet('a' .. 'z'))

        val idParser: Parser<Expr> = TokenParser(idToken) + idToExpr

        val wrappedExprParser: Parser<Expr> = TokenRecognizer.text("(", tokenizer).plus(exprParser).plus(TokenRecognizer.text(")", tokenizer))
        return idParser.or(wrappedExprParser)
    }

    private fun appReducer(term: Parser<Expr>): Reducer<Expr, Expr> = term.plus(
        object : Fold<Expr, Expr, Expr> {
            override fun fold(left: Expr, right: Expr): Expr {
                return left.app(right)
            }

            override fun invertLeft(value: Expr): FnResult<Expr> {
                return FnResult.ofNullable((value as? App)?.left)
            }

            override fun invertRight(value: Expr): FnResult<Expr> {
                return FnResult.ofNullable((value as? App)?.right)
            }
        })
}
