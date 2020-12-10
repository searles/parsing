package at.searles.parsingtools.formatter

import at.searles.lexer.Lexer
import at.searles.lexer.SkipTokenizer
import at.searles.parsing.Parser
import at.searles.parsing.ParserStream
import at.searles.parsing.format.CodeFormatter
import at.searles.parsing.format.EditableString
import at.searles.parsing.format.Mark
import at.searles.parsing.format.Markers
import at.searles.parsing.format.CodePrinter
import at.searles.parsing.printing.StringOutStream
import at.searles.parsing.tokens.TokenRecognizer
import at.searles.parsingtools.common.Init
import at.searles.regexp.CharSet
import at.searles.regexp.Text
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class FormatBacktrackingTest {

    private lateinit var parser: Parser<Pair<String, String>>
    private var blankId: Int = -1

    @Before
    fun setUp() {
        val tokenizer = SkipTokenizer(Lexer())

        blankId = tokenizer.addSkipped(CharSet.chars('\n', ' ').rep())

        val a = TokenRecognizer(tokenizer.add(Text("a")), tokenizer, "a") + Init("A")
        val b = TokenRecognizer(tokenizer.add(Text("b")), tokenizer, "b") + Init("B")

        parser = a + (
                Mark(Markers.Indent) + Mark(Markers.NewLine) + b or
                Mark(Markers.NewLine) + a
        )
    }

    @Test
    fun testSimpleBacktrackingWhenPrintingAst() {
        val parserStream = ParserStream.create("aa")

        val ast = parser.parse(parserStream)!!

        val outstream = StringOutStream()
        val codePrinter = CodePrinter(outstream)

        parser.print(ast)!!.accept(codePrinter)

        Assert.assertEquals("a\na", outstream.toString())
    }


    @Test
    fun testSimpleBacktrackingWhenFormattingEditableText() {
        val text = EditableString("aa")
        val formatter = CodeFormatter(parser, blankId)
        formatter.format(text)

        Assert.assertEquals("a\na", text.toString())
    }
}