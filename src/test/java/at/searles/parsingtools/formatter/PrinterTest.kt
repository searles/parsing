package at.searles.parsingtools.formatter

import at.searles.buf.Frame
import at.searles.lexer.Lexer
import at.searles.lexer.SkipTokenizer
import at.searles.lexer.TokenStream
import at.searles.parsing.*
import at.searles.parsing.Reducer.Companion.rep
import at.searles.parsing.printing.*
import at.searles.parsing.ref.Ref
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
        val stack: Stack<ArrayList<ConcreteSyntaxTree>> = Stack()

        stack.push(ArrayList())

        this.stream.listener = (object: ParserStream.Listener {
            override fun onRefStart(parserStream: ParserStream, label: String) {
                stack.push(ArrayList())
            }

            override fun onRefFail(parserStream: ParserStream, label: String) {
                // we created the list for nothing...
                stack.pop()
                return
            }

            override fun onRefSuccess(parserStream: ParserStream, label: String) {
                val list = stack.pop()
                val cstNode = ListConcreteSyntaxTree(list)
                stack.peek().add(LabelledConcreteSyntaxTree(label, cstNode))
            }
        })

        this.stream.tokStream().setListener(object: TokenStream.Listener {
            override fun tokenConsumed(src: TokenStream, tokenId: Int, frame: Frame) {
                // skip all white spaces
                if(tokenId == whiteSpaceTokId) {
                    return
                }

                // add all other tokens to current top in stack.
                // this also includes other hidden tokens like comments
                // that we normally want to keep when formatting the source 
                // code.
                stack.peek().add(LeafConcreteSyntaxTree(frame.toString()))
            }
        })

        if(!parser.recognize(stream)) {
            output = null
            return
        }

        val cst = ListConcreteSyntaxTree(stack.pop())

        assert(stack.isEmpty())

        cst.printTo(cstPrinter)
        output = outStream.toString()
    }

    private fun actParse() {
        ast = parser.parse(stream)
    }

    private fun actPrint() {
        val cst = parser.print(ast!!)
        cst?.printTo(cstPrinter)
        output = outStream.toString()
    }

    private var whiteSpaceTokId: Int = Integer.MIN_VALUE // invalid default value.
    private lateinit var outStream: StringOutStream
    private lateinit var stream: ParserStream
    private lateinit var parser: Parser<Node>
    private var ast: Node? = null
    private var output: String? = null

    private lateinit var cstPrinter: CstPrinter

    private fun initParser() {
        val lexer = Lexer()
        val tokenizer = SkipTokenizer(lexer)

        whiteSpaceTokId = lexer.add(RegexpParser.parse("[ \n\r\t]+"))
        tokenizer.addSkipped(whiteSpaceTokId)

        val openPar = Recognizer.fromString("(", tokenizer)
        val closePar = Recognizer.fromString(")", tokenizer)

        val idMapping = object : Mapping<CharSequence, Node> {
            override fun parse(stream: ParserStream, input: CharSequence): Node =
                    IdNode(stream.toTrace(), input.toString())

            override fun left(result: Node): CharSequence? =
                    if (result is IdNode) result.value else null
        }

        val numMapping = object : Mapping<CharSequence, Node> {
            override fun parse(stream: ParserStream, input: CharSequence): Node =
                    NumNode(
                            stream.toTrace(),
                            Integer.parseInt(input.toString())
                    )

            override fun left(result: Node): CharSequence? =
                    if (result is NumNode) result.value.toString() else null
        }

        val id = Parser.fromToken(lexer.add(RegexpParser.parse("[a-z]+")), tokenizer, idMapping).ref("id")
        val num = Parser.fromToken(lexer.add(RegexpParser.parse("[0-9]+")), tokenizer, numMapping).ref("num")

        val expr = Ref<Node>("expr")

        // term = id | num | '(' expr ')'
        val term = id.or(num).or(openPar.plus(expr.ref(Markers.Block)).plus(closePar))

        // app = term+
        val appFold = object : Fold<Node, Node, Node> {
            override fun apply(stream: ParserStream, left: Node, right: Node): Node {
                return AppNode(stream.toTrace(), left, right)
            }

            override fun leftInverse(result: Node): Node? {
                return if (result is AppNode) result.left else null
            }

            override fun rightInverse(result: Node): Node? {
                return if (result is AppNode) result.right else null
            }
        }

        val app = term.plus(term.ref(Markers.Arg).plus(appFold).rep()).ref("app")

        expr.ref = app

        parser = expr
    }

    private fun initCstPrinter() {
        this.outStream = StringOutStream()
        this.cstPrinter = object : CstPrinter(outStream) {
            var indent: Int = 0
            var atBeginningOfLine: Boolean = false

            private fun newline() {
                append("\n")
                atBeginningOfLine = true
            }

            override fun print(tree: ConcreteSyntaxTree, label: String): CstPrinter {
                return when (label) {
                    Markers.Block -> {
                        newline()
                        indent++
                        print(tree)
                        indent--
                        newline()
                        return this
                    }
                    Markers.Arg -> {
                        append(" ").print(tree)
                    }
                    else -> print(tree)
                }
            }

            override fun print(seq: CharSequence): CstPrinter {
                if (atBeginningOfLine) {
                    atBeginningOfLine = false
                    append(" ".repeat(indent))
                }

                return append(seq)
            }
        }
    }

    abstract class Node(val trace: Trace)

    class NumNode(trace: Trace, val value: Int) : Node(trace)
    class IdNode(trace: Trace, val value: String) : Node(trace)

    class AppNode(trace: Trace, val left: Node, val right: Node) : Node(trace)

    object Markers {
        const val Block = "block"
        const val Arg = "arg"
    }
}
