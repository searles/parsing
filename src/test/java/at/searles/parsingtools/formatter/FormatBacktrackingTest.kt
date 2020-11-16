package at.searles.parsingtools.formatter

import at.searles.lexer.Lexer
import at.searles.lexer.SkipTokenizer
import at.searles.parsing.Parser
import at.searles.parsing.ParserStream
import at.searles.parsing.format.FormatRules
import at.searles.parsing.format.Mark
import at.searles.parsing.printing.CodePrinter
import at.searles.parsing.printing.StringOutStream
import at.searles.parsing.tokens.TokenRecognizer
import at.searles.parsingtools.common.Init
import at.searles.regexp.CharSet
import at.searles.regexp.Text
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.lang.StringBuilder

class FormatBacktrackingTest {

    private lateinit var parser: Parser<Pair<String, String>>
    private lateinit var rules: FormatRules
    private var blankId: Int = -1

    @Before
    fun setUp() {
        val tokenizer = SkipTokenizer(Lexer())

        blankId = tokenizer.addSkipped(CharSet.chars('\n', ' ').rep())

        val a = TokenRecognizer(tokenizer.add(Text("a")), tokenizer, "a") + Init("A")
        val b = TokenRecognizer(tokenizer.add(Text("b")), tokenizer, "b") + Init("B")

        val newline = "newline"
        val indent = "indent"

        parser = a + (
                Mark(indent) + Mark(newline) + b or
                Mark(newline) + a
        )

        rules = FormatRules().apply {
            addRule(newline) { it.insertNewLine() }
            addRule(indent) { it.indent() }
        }
    }

    @Test
    fun testSimpleBacktrackingWhenPrintingAst() {
        val parserStream = ParserStream.create("aa")

        val ast = parser.parse(parserStream)!!

        val outstream = StringOutStream()
        val codePrinter = CodePrinter(rules, outstream)

        parser.print(ast)!!.accept(codePrinter)

        Assert.assertEquals("a\na", outstream.toString())
    }


    @Test
    fun testSimpleBacktrackingWhenFormattingEditableText() {
        val text = EditableString("aa")
        val formatter = CodeFormatter(rules, parser, blankId)
        formatter.format(text)

        Assert.assertEquals("a\na", text.toString())
    }
}