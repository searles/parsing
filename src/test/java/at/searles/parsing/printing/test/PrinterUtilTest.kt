package at.searles.parsing.printing.test

import at.searles.lexer.LexerWithHidden
import at.searles.parsing.Mapping
import at.searles.parsing.Parser
import at.searles.parsing.ParserStream
import at.searles.parsing.Recognizer
import at.searles.parsing.printing.CstPrinter
import at.searles.parsing.printing.StringOutStream
import at.searles.parsing.utils.Utils
import at.searles.parsing.utils.ast.AstNode
import at.searles.parsing.utils.ast.SourceInfo
import at.searles.regex.RegexParser
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
        this.stream = ParserStream.fromString(input)
    }

    private fun actParse() {
        ast = parser.parse(stream)
    }

    private fun actPrint() {
        val cst = parser.print(ast)
        cst?.printTo(cstPrinter)
        output = outStream.toString()
    }

    private var whiteSpaceTokId: Int = Integer.MIN_VALUE // invalid default value.
    private lateinit var outStream: StringOutStream
    private lateinit var stream: ParserStream
    private lateinit var parser: Parser<AstNode>
    private var ast: AstNode? = null
    private var output: String? = null

    private lateinit var cstPrinter: CstPrinter

    private fun initParserUtilList(mayBeEmpty: Boolean, hasSeparator: Boolean) {
        val lexer = LexerWithHidden()

        whiteSpaceTokId = lexer.addHiddenToken(RegexParser.parse("[ \n\r\t]+"))

        val idMapping = object : Mapping<CharSequence, AstNode> {
            override fun parse(stream: ParserStream, left: CharSequence): AstNode =
                    IdNode(stream.createSourceInfo(), left.toString())

            override fun left(result: AstNode): CharSequence? =
                    if (result is IdNode) result.value else null
        }

        val vecMapping = object: Mapping<List<AstNode>, AstNode> {
            override fun parse(stream: ParserStream, left: List<AstNode>): AstNode {
                return VecNode(stream.createSourceInfo(), left)
            }

            override fun left(result: AstNode): List<AstNode>? {
                return if (result is VecNode) result.left else null
            }
        }

        val id = Parser.fromToken(lexer.token(RegexParser.parse("[a-z]+")), idMapping, false).ref("id")

        parser = if (hasSeparator)
            if (mayBeEmpty)
                Utils.list(id, Recognizer.fromString(",", lexer, false)).then(vecMapping)
            else
                Utils.list1(id, Recognizer.fromString(",", lexer, false)).then(vecMapping)
        else
            if (mayBeEmpty)
                Utils.list(id).then(vecMapping)
            else
                Utils.list1(id).then(vecMapping)
    }

    private fun initCstPrinter() {
        this.outStream = StringOutStream()
        this.cstPrinter = CstPrinter(outStream);
    }

    class IdNode(info: SourceInfo, val value: String) : AstNode(info)
    class VecNode(info: SourceInfo, val left: List<AstNode>) : AstNode(info)
}
