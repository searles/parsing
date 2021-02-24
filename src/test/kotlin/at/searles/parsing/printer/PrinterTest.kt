package at.searles.parsing.printer

import at.searles.parsing.lexer.Lexer
import at.searles.parsing.lexer.regexp.CharSet
import at.searles.parsing.lexer.regexp.Text
import at.searles.parsing.parser.*
import at.searles.parsing.parser.combinators.TokenParser
import at.searles.parsing.parser.combinators.TokenRecognizer
import at.searles.parsing.parser.tools.Print
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class PrinterTest {
    private lateinit var lexer: Lexer
    private lateinit var number: Parser<CharSequence>
    private lateinit var toIntNode: Conversion<CharSequence, Tree>
    private lateinit var plusSign: Recognizer
    private lateinit var intParser: Parser<Tree>
    private lateinit var additionOp: Fold<Tree, Tree, Tree>
    private lateinit var addition: Parser<Tree>

    @Before
    fun setUp() {
        lexer = Lexer()
        number = TokenParser(lexer.createToken(CharSet('0'..'9').rep1()))
        toIntNode = object: Conversion<CharSequence, Tree> {
            override fun convert(value: CharSequence): Tree.IntNode {
                return Tree.IntNode(value.toString().toInt())
            }

            override fun invert(value: Tree): FnResult<String> {
                return FnResult.ofNullable((value as? Tree.IntNode)?.value.toString())
            }
        }
        plusSign = TokenRecognizer(lexer.createToken(Text("+")), "+")
        intParser = number + toIntNode

        additionOp = object: Fold<Tree, Tree, Tree> {
            override fun fold(left: Tree, right: Tree): Tree {
                return Tree.AddNode(left, right)
            }

            override fun invertLeft(value: Tree): FnResult<Tree> {
                return FnResult.ofNullable((value as? Tree.AddNode)?.left)
            }

            override fun invertRight(value: Tree): FnResult<Tree> {
                return FnResult.ofNullable((value as? Tree.AddNode)?.right)
            }
        }

        addition = intParser + (plusSign + intParser + additionOp)
    }

    @Test
    fun testPrinter() {
        val result = addition.parse(ParserStream("16+32"))

        Assert.assertTrue(result.isSuccess)

        val printResult = addition.print(result.value)
        Assert.assertTrue(printResult.isSuccess)
        Assert.assertEquals("16+32", printResult.toString())
    }

    @Test
    fun testPrinterWithMarks() {
        val space = Print { it.append(" ") }

        addition = intParser + ((space + plusSign) + space + intParser + additionOp)

        val result = addition.parse(ParserStream("16+32"))

        Assert.assertTrue(result.isSuccess)

        val printResult = addition.print(result.value)
        Assert.assertTrue(printResult.isSuccess)

        val output = StringOutStream().also {
            printResult.print(it)
        }.toString()

        Assert.assertEquals("16 + 32", output)
    }
}