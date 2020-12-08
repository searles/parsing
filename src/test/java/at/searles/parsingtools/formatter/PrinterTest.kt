package at.searles.parsingtools.formatter

import at.searles.buf.Frame
import at.searles.lexer.Lexer
import at.searles.lexer.SkipTokenizer
import at.searles.lexer.TokenStream
import at.searles.parsing.*
import at.searles.parsing.Reducer.Companion.rep
import at.searles.parsing.format.FormatRules
import at.searles.parsing.format.Mark
import at.searles.parsing.printing.*
import at.searles.parsing.ref.RefParser
import at.searles.regexparser.RegexpParser
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.util.*

class PrinterTest {

    @Before
    fun setUp() {
        initParser()
        initCstPrinter()
    }

    @Test
    fun constantTest() {
        initParser()
        withInput("a")
        actParse()
        actPrint()

        Assert.assertEquals("a", output)
    }

    @Test
    fun constantFormatTest() {
        initParser()
        withInput("a")
        actFormat()

        Assert.assertEquals("a", output)
    }

    @Test
    fun appTest() {
        initParser()
        withInput("a b")
        actParse()
        actPrint()

        Assert.assertEquals("a b", output)
    }

    @Test
    fun appFormatTest() {
        initParser()
        withInput("  a   b  ")
        actFormat()

        Assert.assertEquals("a b", output)
    }

    @Test
    fun longAppTest() {
        initParser()
        withInput("a b c d")
        actParse()
        actPrint()

        Assert.assertEquals("a b c d", output)
    }

    @Test
    fun appInAppTest() {
        initParser()
        withInput("a ( b c )")
        actParse()
        actPrint()

        Assert.assertEquals("a (\n b c\n)", output)
    }

    @Test
    fun appInAppFormatTest() {
        initParser()
        withInput("a ( b c )")
        actFormat()

        Assert.assertEquals("a (\n b c\n)", output)
    }

    @Test
    fun manyEmbeddedAppsTest() {
        initParser()
        withInput("a ( b ( c d (e (f g h(i j) k (l m n) ) ) o p (q r (s t))))")
        actParse()
        actPrint()

        Assert.assertEquals("a (\n" +
                " b (\n" +
                "  c d (\n" +
                "   e (\n" +
                "    f g h (\n" +
                "     i j\n" +
                "    ) k (\n" +
                "     l m n\n" +
                "    )\n" +
                "   )\n" +
                "  ) o p (\n" +
                "   q r (\n" +
                "    s t\n" +
                "   )\n" +
                "  )\n" +
                " )\n" +
                ")", output)
    }

    @Test
    fun manyEmbeddedAppsFormatTest() {
        initParser()
        withInput("a ( b ( c d (e (f g h(i j) k (l m n) ) ) o p (q r (s t))))")
        actFormat()

        Assert.assertEquals("a (\n" +
                " b (\n" +
                "  c d (\n" +
                "   e (\n" +
                "    f g h (\n" +
                "     i j\n" +
                "    ) k (\n" +
                "     l m n\n" +
                "    )\n" +
                "   )\n" +
                "  ) o p (\n" +
                "   q r (\n" +
                "    s t\n" +
                "   )\n" +
                "  )\n" +
                " )\n" +
                ")", output)
    }

    private fun withInput(input: String) {
        this.stream = ParserStream.create(input)
    }

    private fun actFormat() {
        val list = ArrayList<ConcreteSyntaxTree>()

        this.stream.listener = (object: ParserStream.Listener {
            override fun onMark(marker: Any, parserStream: ParserStream) {
                list.add(FormatTree(marker))
            }
        })

        this.stream.tokenStream.listener = object: TokenStream.Listener {
            override fun tokenConsumed(src: TokenStream, tokenId: Int, frame: Frame) {
                // skip all white spaces
                if(tokenId == whiteSpaceTokId) {
                    return
                }

                // add all other tokens to current top in stack.
                // this also includes other hidden tokens like comments
                // that we normally want to keep when formatting the source 
                // code.
                list.add(TokenTree(frame.toString()))
            }
        }

        if(!parser.recognize(stream)) {
            output = null
            return
        }

        ListTree(list).accept(simplePrinter)
        output = outStream.toString()
    }

    private fun actParse() {
        ast = parser.parse(stream)
    }

    private fun actPrint() {
        val cst = parser.print(ast!!)
        cst?.accept(simplePrinter)
        output = outStream.toString()
    }

    private var whiteSpaceTokId: Int = Integer.MIN_VALUE // invalid default value.
    private lateinit var outStream: StringOutStream
    private lateinit var stream: ParserStream
    private lateinit var parser: Parser<Node>
    private var ast: Node? = null
    private var output: String? = null

    private lateinit var simplePrinter: CstVisitor

    private fun initParser() {
        val lexer = Lexer()
        val tokenizer = SkipTokenizer(lexer)

        whiteSpaceTokId = tokenizer.addSkipped(RegexpParser.parse("[ \n\r\t]+"))

        val openPar = Recognizer.fromString("(", tokenizer)
        val closePar = Recognizer.fromString(")", tokenizer)

        val idMapping = object : Mapping<CharSequence, Node> {
            override fun parse(left: CharSequence, stream: ParserStream): Node =
                    IdNode(stream.createTrace(), left.toString())

            override fun left(result: Node): CharSequence? =
                    if (result is IdNode) result.value else null
        }

        val numMapping = object : Mapping<CharSequence, Node> {
            override fun parse(left: CharSequence, stream: ParserStream): Node =
                    NumNode(
                            stream.createTrace(),
                            Integer.parseInt(left.toString())
                    )

            override fun left(result: Node): CharSequence? =
                    if (result is NumNode) result.value.toString() else null
        }

        val id = Parser.fromToken(lexer.add(RegexpParser.parse("[a-z]+")), tokenizer, idMapping).ref("id")
        val num = Parser.fromToken(lexer.add(RegexpParser.parse("[0-9]+")), tokenizer, numMapping).ref("num")

        val expr = RefParser<Node>("expr")

        // term = id | num | '(' expr ')'
        val term = id or
                num or
                openPar + Mark(Markers.Indent) + expr + Mark(Markers.Unindent) + closePar

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

        val app = term + (Mark(Markers.Separator) + term + appFold).rep()

        expr.ref = app

        parser = expr
    }

    private fun initCstPrinter() {
        this.outStream = StringOutStream()

        val rules = FormatRules().apply() {
            indentation = " "

            addRule(Markers.Indent) { it.insertNewLine() ; it.indent() }
            addRule(Markers.Unindent) { it.insertNewLine() ; it.unindent() }
            addRule(Markers.Separator) { it.insertSpace() }

            indentation = " "

        }

        this.simplePrinter = CodePrinter(rules, outStream)
    }

    abstract class Node(val trace: Trace)

    class NumNode(trace: Trace, val value: Int) : Node(trace)
    class IdNode(trace: Trace, val value: String) : Node(trace)

    class AppNode(trace: Trace, val left: Node, val right: Node) : Node(trace)

    enum class Markers {
        Indent, Unindent, Separator
    }
}
