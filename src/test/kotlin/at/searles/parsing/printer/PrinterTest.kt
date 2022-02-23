package at.searles.parsing.printer

import at.searles.parsing.lexer.Lexer
import at.searles.parsing.lexer.TokenStream
import at.searles.parsing.lexer.regexp.CharSet
import at.searles.parsing.lexer.regexp.Text
import at.searles.parsing.parser.*
import at.searles.parsing.parser.combinators.TokenParser
import at.searles.parsing.parser.combinators.TokenRecognizer
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class PrinterTest {
    private lateinit var lexer: Lexer
    private lateinit var toIntNode: Conversion<CharSequence, Tree>
    private lateinit var plusSign: Recognizer
    private lateinit var intParser: Parser<Tree>
    private lateinit var additionOp: Fold<Tree, Tree, Tree>
    private lateinit var addition: Parser<Tree>

    @Before
    fun setUp() {
        lexer = Lexer()

        toIntNode = object: Conversion<CharSequence, Tree> {
            override fun convert(value: CharSequence): Tree.IntNode {
                return Tree.IntNode(value.toString().toInt())
            }

            override fun invert(value: Tree): FnResult<String> {
                return FnResult.ofNullable((value as? Tree.IntNode)?.value.toString())
            }
        }

        intParser = TokenParser(lexer.createToken(CharSet('0'..'9').rep1()), lexer, toIntNode)

        plusSign = TokenRecognizer(lexer.createToken(Text("+")), lexer, "+")

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
        val result = addition.parse(TokenStream("16+32"))

        Assert.assertTrue(result.isSuccess)

        val printResult = addition.print(result.value)
        Assert.assertTrue(printResult.isSuccess)
        Assert.assertEquals("16+32", printResult.toString())
    }
//
//    @Test
//    fun testPrinterWithMarks() {
//        fun space(it: OutStream) { it.append(" ") }
//
//        addition = intParser + ((Mark("space") + plusSign) + Mark("space") + intParser + additionOp)
//
//        val result = addition.parse(TokenStream("16+32"))
//
//        Assert.assertTrue(result.isSuccess)
//
//        val printResult = addition.print(result.value)
//        Assert.assertTrue(printResult.isSuccess)
//
//        val output = object: StringOutStream() {
//            override fun mark(label: Any) {
//                if(label == "space") space(this)
//            }
//        }.also {
//            printResult.print(it)
//        }.toString()
//
//        Assert.assertEquals("16 + 32", output)
//    }
}