package at.searles.parsingtools.formatter

import at.searles.lexer.Lexer
import at.searles.lexer.SkipTokenizer
import at.searles.parsing.*
import at.searles.parsing.Reducer.Companion.rep
import at.searles.parsing.format.FormatRules
import at.searles.parsing.format.Mark
import at.searles.parsing.ref.RefParser
import at.searles.regexparser.RegexpParser
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class FormatterTest {

    @Before
    fun setUp() {
        initParser()
    }

    @Test
    fun constantFormatTest() {
        initParser()
        withInput("(a)")
        actFormat()

        Assert.assertEquals("(\n    a\n)\n", source.toString())
    }

    @Test
    fun appFormatTest() {
        initParser()
        withInput("a(b)c")
        actFormat()

        Assert.assertEquals("a (\n" +
                "    b\n" +
                ")\n" +
                "c\n", source.toString())
    }

    @Test
    fun appFormatStabilityTest() {
        initParser()
        withInput("(a)")
        actFormat()

        Assert.assertEquals("(\n" +
                "    a\n" +
                ")\n", source.toString())
    }

    @Test
    fun constantFormatWithSpaceTest() {
        initParser()
        withInput("( a)")
        actFormat()

        Assert.assertEquals("(\n    a\n)\n", source.toString())
    }

    @Test
    fun appInAppFormatTest() {
        initParser()
        withInput("a ( b\n c )")
        actFormat()

        Assert.assertEquals("a (\n" +
                "    b\n" +
                "    c\n" +
                ")\n", source.toString())
    }

    @Test
    fun appInSingleAppFormatTest() {
        initParser()
        withInput("a(c )")
        actFormat()

        Assert.assertEquals("a (\n" +
                "    c\n" +
                ")\n", source.toString())
    }

    @Test
    fun manyEmbeddedAppsFormatTest() {
        initParser()
        withInput("a (\n b ( c d\n (e (f g h(i \n\nj) k (l \nm n)\n )\n ) o\n p (\nq r (\ns t)\n)\n)\n)\n\n")
        actFormat()

        Assert.assertEquals("a (\n" +
                "    b (\n" +
                "        c d\n" +
                "        (\n" +
                "            e (\n" +
                "                f g h (\n" +
                "                    i\n" +
                "\n" +
                "                    j\n" +
                "                )\n" +
                "                k (\n" +
                "                    l\n" +
                "                    m n\n" +
                "                )\n" +
                "            )\n" +
                "        )\n" +
                "        o\n" +
                "        p (\n" +
                "            q r (\n" +
                "                s t\n" +
                "            )\n" +
                "        )\n" +
                "    )\n" +
                ")\n\n", source.toString())
    }

    private fun withInput(input: String) {
        this.source = input
    }

    private fun actFormat() {
        val rules = FormatRules().apply {
            addRule(Markers.Indent) { it.indent() }
            addRule(Markers.Unindent) { it.unindent() }
            addRule(Markers.Newline) { it.insertNewLine() }
            addRule(Markers.Space) { it.insertSpace() }
        }

        val formatter = CodeFormatter(rules, parser, whiteSpaceTokId)
        formatter.format(EditableString(source))
    }

    private var whiteSpaceTokId: Int = Integer.MIN_VALUE // invalid default value.
    private lateinit var source: String
    private lateinit var parser: Parser<Node>

    private fun initParser() {
        val lexer = Lexer()
        val tokenizer = SkipTokenizer(lexer)

        whiteSpaceTokId = tokenizer.addSkipped(RegexpParser.parse("[ \n\r\t]+"))

        val openPar = Recognizer.fromString("(", tokenizer)
        val closePar = Recognizer.fromString(")", tokenizer)

        val idMapping = object : Mapping<CharSequence, Node> {
            override fun parse(stream: ParserStream, input: CharSequence): Node =
                IdNode(stream.createTrace(), input.toString())

            override fun left(result: Node): CharSequence? =
                    if (result is IdNode) result.value else null
        }

        val numMapping = object : Mapping<CharSequence, Node> {
            override fun parse(stream: ParserStream, input: CharSequence): Node =
                NumNode(
                    stream.createTrace(),
                    Integer.parseInt(input.toString())
                )

            override fun left(result: Node): CharSequence? =
                    if (result is NumNode) result.value.toString() else null
        }

        val id = Parser.fromToken(lexer.add(RegexpParser.parse("[a-z]+")), tokenizer, idMapping).ref("id")
        val num = Parser.fromToken(lexer.add(RegexpParser.parse("[0-9]+")), tokenizer, numMapping).ref("num")

        val expr = RefParser<Node>("expr")

        // term = id | num | '(' expr ')'
        val term = (
                id or
                num or (
                        openPar + Mark(Markers.Newline) + Mark(Markers.Indent) + expr + Mark(Markers.Unindent) + Mark(Markers.Newline) + closePar + Mark(Markers.Newline)
                )
        ) + Mark(Markers.Space)

        // app = term+
        val appFold = object : Fold<Node, Node, Node> {
            override fun apply(stream: ParserStream, left: Node, right: Node): Node {
                return AppNode(stream.createTrace(), left, right)
            }

            override fun leftInverse(result: Node): Node? {
                return if (result is AppNode) result.left else null
            }

            override fun rightInverse(result: Node): Node? {
                return if (result is AppNode) result.right else null
            }
        }

        val app = (term + (term + appFold).rep()).ref("app")

        expr.ref = app

        parser = expr
    }

    abstract class Node(val trace: Trace)

    class NumNode(trace: Trace, val value: Int) : Node(trace)
    class IdNode(trace: Trace, val value: String) : Node(trace)

    class AppNode(trace: Trace, val left: Node, val right: Node) : Node(trace)

    enum class  Markers {
        Indent, Unindent, Space, Newline
    }
}
