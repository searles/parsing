package at.searles.parsingtools.formatter

import at.searles.lexer.Lexer
import at.searles.lexer.SkipTokenizer
import at.searles.parsing.*
import at.searles.parsing.printing.CstPrinter
import at.searles.parsing.printing.StringOutStream
import at.searles.regexparser.RegexpParser
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class PrinterUtilTest {

    @Before
    fun setUp() {
        initCstPrinter()
    }

    @Test
    fun singleTTTest() {
        initParserUtilList(true, true)
        withInput("a")
        actParse()
        actPrint()

        Assert.assertEquals("a", output)
    }

    @Test
    fun sequenceTTTest() {
        initParserUtilList(true, true)
        withInput("a, b")
        actParse()
        actPrint()

        Assert.assertEquals("a,b", output)
    }

    @Test
    fun emptySequenceTTTest() {
        initParserUtilList(true, true)
        withInput("")
        actParse()
        actPrint()

        Assert.assertEquals("", output)
    }

    @Test
    fun singleFTTest() {
        initParserUtilList(false, true)
        withInput("a")
        actParse()
        actPrint()

        Assert.assertEquals("a", output)
    }

    @Test
    fun sequenceFTTest() {
        initParserUtilList(false, true)
        withInput("a, b")
        actParse()
        actPrint()

        Assert.assertEquals("a,b", output)
    }

    @Test
    fun singleFFTest() {
        initParserUtilList(false, false)
        withInput("a")
        actParse()
        actPrint()

        Assert.assertEquals("a", output)
    }

    @Test
    fun sequenceFFTest() {
        initParserUtilList(false, false)
        withInput("a b")
        actParse()
        actPrint()

        Assert.assertEquals("ab", output)
    }

    @Test
    fun singleTFTest() {
        initParserUtilList(true, false)
        withInput("a")
        actParse()
        actPrint()

        Assert.assertEquals("a", output)
    }

    @Test
    fun sequenceTFTest() {
        initParserUtilList(true, false)
        withInput("a b")
        actParse()
        actPrint()

        Assert.assertEquals("ab", output)
    }

    @Test
    fun emptySequenceTFTest() {
        initParserUtilList(true, false)
        withInput("")
        actParse()
        actPrint()

        Assert.assertEquals("", output)
    }


    private fun withInput(input: String) {
        this.stream = ParserStream.create(input)
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

    private fun initParserUtilList(mayBeEmpty: Boolean, hasSeparator: Boolean) {
        val tokenizer = SkipTokenizer(Lexer())

        whiteSpaceTokId = tokenizer.add(RegexpParser.parse("[ \n\r\t]+"))
        tokenizer.addSkipped(whiteSpaceTokId)

        val idMapping = object : Mapping<CharSequence, Node> {
            override fun parse(stream: ParserStream, input: CharSequence): Node =
                    IdNode(stream.toTrace(), input.toString())

            override fun left(result: Node): CharSequence? =
                    if (result is IdNode) result.value else null
        }

        val vecMapping = object: Mapping<List<Node>, Node> {
            override fun parse(stream: ParserStream, input: List<Node>): Node {
                return VecNode(stream.toTrace(), input)
            }

            override fun left(result: Node): List<Node>? {
                return if (result is VecNode) result.left else null
            }
        }

        val id = Parser.fromRegex(RegexpParser.parse("[a-z]+"), tokenizer, idMapping).ref("id")

        parser = if (hasSeparator)
            if (mayBeEmpty)
                id.rep(Recognizer.fromString(",", tokenizer)).plus(vecMapping)
            else
                id.rep1(Recognizer.fromString(",", tokenizer)).plus(vecMapping)
        else
            if (mayBeEmpty)
                id.rep().plus(vecMapping)
            else
                id.rep1().plus(vecMapping)
    }

    private fun initCstPrinter() {
        this.outStream = StringOutStream()
        this.cstPrinter = CstPrinter(outStream)
    }

    abstract class Node(val trace: Trace)
    class IdNode(trace: Trace, val value: String) : Node(trace)
    class VecNode(trace: Trace, val left: List<Node>) : Node(trace)
}
