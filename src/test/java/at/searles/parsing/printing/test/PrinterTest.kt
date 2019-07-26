package at.searles.parsing.printing.test

import at.searles.demo.ParserException
import at.searles.lexer.LexerWithHidden
import at.searles.parsing.*
import at.searles.parsing.printing.ConcreteSyntaxTree
import at.searles.parsing.printing.CstPrinter
import at.searles.parsing.printing.StringOutStream
import at.searles.parsing.utils.ast.AstNode
import at.searles.parsing.utils.ast.SourceInfo
import at.searles.regex.RegexParser
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class PrinterTest {

    @Before
    fun setUp() {
        initParser()
        initCstPrinter()
        env = Environment { stream, failedParser ->
            throw ParserException(
                    "Error at ${stream.offset()}, expected ${failedParser.right()}"
            )
        }
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
    fun appTest() {
        initParser()
        withInput("a b")
        actParse()
        actPrint()

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

    private fun withInput(input: String) {
        this.stream = ParserStream.fromString(input)
    }

    private fun actParse() {
        ast = parser.parse(env, stream)
    }

    private fun actPrint() {
        val cst = parser.print(env, ast)
        cst?.print(cstPrinter)
        output = outStream.toString()
    }

    private lateinit var outStream: StringOutStream
    private lateinit var stream: ParserStream
    private lateinit var env: Environment
    private lateinit var parser: Parser<AstNode>
    private var ast: AstNode? = null
    private var output: String? = null

    private lateinit var cstPrinter: CstPrinter

    fun initParser() {
        val lexer = LexerWithHidden()

        lexer.hiddenToken(RegexParser.parse("[ \n\r\t]+"))

        val openPar = Recognizer.fromString("(", lexer, false)
        val closePar = Recognizer.fromString(")", lexer, false)

        val idMapping = object: Mapping<CharSequence, AstNode> {
            override fun parse(env: Environment, left: CharSequence, stream: ParserStream): AstNode =
                    IdNode(stream.createSourceInfo(), left.toString())

            override fun left(env: Environment, result: AstNode): CharSequence? =
                    if (result is IdNode) result.value else null
        }

        val numMapping = object: Mapping<CharSequence, AstNode> {
            override fun parse(env: Environment, left: CharSequence, stream: ParserStream): AstNode =
                    NumNode(stream.createSourceInfo(), Integer.parseInt(left.toString()))

            override fun left(env: Environment, result: AstNode): CharSequence? =
                    if (result is NumNode) result.value.toString() else null
        }

        val id = Parser.fromToken(lexer.token(RegexParser.parse("[a-z]+")), idMapping, false).ref("id")
        val num = Parser.fromToken(lexer.token(RegexParser.parse("[0-9]+")), numMapping, false).ref("num")

        val expr = Ref<AstNode>("expr")

        // term = id | num | '(' expr ')'
        val term = id.or(num).or(openPar.then(expr.annotate(Markers.Block)).then(closePar))

        // app = term+
        val appFold = object: Fold<AstNode, AstNode, AstNode> {
            override fun apply(env: Environment, left: AstNode, right: AstNode, stream: ParserStream): AstNode {
                return AppNode(stream.createSourceInfo(), left, right)
            }

            override fun leftInverse(env: Environment, result: AstNode): AstNode? {
                return if(result is AppNode) result.left else null
            }

            override fun rightInverse(env: Environment, result: AstNode): AstNode? {
                return if(result is AppNode) result.right else null
            }
        }

        val app = term.then(Reducer.rep(term.annotate(Markers.Arg).fold(appFold))).ref("app")

        expr.set(app)

        parser = expr
    }

    fun initCstPrinter() {
        this.outStream = StringOutStream()
        this.cstPrinter = object: CstPrinter(outStream) {
            var indent: Int = 0
            var atBeginningOfLine: Boolean = false

            private fun newline() {
                outStream.append("\n")
                atBeginningOfLine = true
            }

            override fun print(tree: ConcreteSyntaxTree, annotation: Any): CstPrinter {
                return when(annotation) {
                    Markers.Block -> {
                        newline()
                        indent++
                        print(tree)
                        indent--
                        newline()
                        return this
                    }
                    Markers.Arg -> {
                        print(" ").print(tree)
                    }
                    else -> print(tree)
                }
            }

            override fun print(seq: CharSequence?): CstPrinter {
                if(atBeginningOfLine) {
                    atBeginningOfLine = false
                    super.print(" ".repeat(indent))
                }

                return super.print(seq)
            }
        }
    }

    class NumNode(info: SourceInfo, val value: Int): AstNode(info)
    class IdNode(info: SourceInfo, val value: String): AstNode(info)

    class AppNode(info: SourceInfo, val left: AstNode, val right: AstNode): AstNode(info)

    enum class Markers { Block, Arg }
}
