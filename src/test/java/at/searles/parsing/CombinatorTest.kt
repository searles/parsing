package at.searles.parsing

import at.searles.lexer.Lexer
import at.searles.parsing.Parser.Companion.fromRegex
import at.searles.parsing.ParserStream.Companion.createParserStream
import at.searles.parsing.Reducer.Companion.plus
import at.searles.parsing.printing.ConcreteSyntaxTree
import at.searles.parsing.printing.EmptyConcreteSyntaxTree
import at.searles.regexparser.StringToRegex
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class CombinatorTest {
    private val tokenizer = Lexer()
    private val chr = fromRegex(StringToRegex.parse("[a-z]"), tokenizer, false, ToString)
    private val comma = Recognizer.fromString(",", tokenizer, false)
    private val emptyString: Initializer<String> = object : Initializer<String> {
        override fun parse(stream: ParserStream): String {
            return ""
        }

        override fun print(item: String): ConcreteSyntaxTree? {
            return if (item.isEmpty()) EmptyConcreteSyntaxTree() else null
        }
    }
    private val appendSingleChar: Fold<String, String, String> = object : Fold<String, String, String> {
        override fun apply(stream: ParserStream, left: String, right: String): String {
            return left + right
        }

        override fun leftInverse(result: String): String? {
            return if (result.isNotEmpty()) result.substring(0, result.length - 1) else null
        }

        override fun rightInverse(result: String): String? {
            return if (result.isNotEmpty()) result.substring(result.length - 1) else null
        }

        override fun toString(): String {
            return "{append_single_char}"
        }
    }
    private lateinit var parser: Parser<String>
    private lateinit var input: ParserStream
    private var parseResult: String? = null
    private var printResult: ConcreteSyntaxTree? = null
    private var isError = false

    @Before
    fun setUp() {
        isError = false
    }

    // These tests run on a parser that simply parses single chars and
    // attaches these single chars using a simple fold.
    @Test
    fun plus1FailTest() {
        // chr+
        withParser(emptyString.plus(chr.fold(appendSingleChar).plus()))
        withInput("")
        actParse()
        Assert.assertFalse(isError)
        Assert.assertNull(parseResult)
    }

    @Test
    fun plus1SuccessTest() {
        // chr+
        withParser(emptyString.plus(chr.fold(appendSingleChar).plus()))
        withInput("abc")
        actParse()
        actPrint()
        Assert.assertFalse(isError)
        Assert.assertEquals("abc", parseResult)
        Assert.assertEquals("abc", printResult.toString())
    }

    @Test
    fun joinFailTest() {
        // chr+
        withParser(emptyString.plus(comma.join(chr.fold(appendSingleChar))))
        withInput("a,,")
        actParse()
        Assert.assertTrue(isError)
    }

    @Test
    fun joinSingleCharTest() {
        // chr+
        withParser(emptyString.plus(comma.join(chr.fold(appendSingleChar))))
        withInput("a")
        actParse()
        actPrint()
        Assert.assertFalse(isError)
        Assert.assertEquals("a", parseResult)
        Assert.assertEquals("a", printResult.toString())
    }

    @Test
    fun joinMultiCharTest() {
        // chr+
        withParser(emptyString.plus(comma.join(chr.fold(appendSingleChar))))
        withInput("a,b,c")
        actParse()
        actPrint()
        Assert.assertFalse(isError)
        Assert.assertEquals("abc", parseResult)
        Assert.assertEquals("a,b,c", printResult.toString())
    }

    private fun actPrint() {
        printResult = parser.print(parseResult!!)
    }

    private fun actParse() {
        try {
            parseResult = parser.parse(input)
        } catch (ignored: BacktrackNotAllowedException) {
            isError = true
        }
    }

    private fun withInput(input: String) {
        this.input = input.createParserStream().apply {
            isBacktrackAllowed = false
        }
    }

    private fun withParser(parser: Parser<String>) {
        this.parser = parser
    }

    companion object {
        private val ToString: Mapping<CharSequence, String> = object : Mapping<CharSequence, String> {
            override fun parse(stream: ParserStream, input: CharSequence): String {
                return input.toString()
            }

            override fun left(result: String): CharSequence? {
                return result
            }
        }
    }
}