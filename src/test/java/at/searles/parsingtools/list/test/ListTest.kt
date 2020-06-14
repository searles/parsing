package at.searles.parsingtools.list.test

import at.searles.lexer.Lexer
import at.searles.parsing.*
import at.searles.parsing.ParserStream.Companion.createParserStream
import at.searles.parsingtools.list.EmptyListCreator
import at.searles.parsingtools.list.ImmutableList
import at.searles.regexparser.StringToRegex
import org.junit.Assert
import org.junit.Test

/**
 * Test grouping of items with same type: B1,2,3:Aa,b:B1:B1,2:Aa:Ab
 * use case: Grouping of var declarations in program declarations.
 */
class ListTest {

    private val tokenizer = Lexer()
    private val id =
        Parser.fromRegex(StringToRegex.parse("[a-z]+"), tokenizer, false, object : Mapping<CharSequence, Any> {
            override fun parse(stream: ParserStream, input: CharSequence): Any {
                return input.toString()
            }

            override fun left(result: Any): CharSequence? {
                return if (result is String) result.toString() else null
            }
        })

    private val num =
        Parser.fromRegex(StringToRegex.parse("[0-9]+"), tokenizer, false, object : Mapping<CharSequence, Any> {
            override fun parse(stream: ParserStream, input: CharSequence): Any {
                return Integer.parseInt(input.toString())
            }

            override fun left(result: Any): CharSequence? {
                return if (result is Int) result.toString() else null
            }
        })

    private val add = object : Fold<List<Any>, Any, List<Any>> {
        override fun apply(stream: ParserStream, left: List<Any>, right: Any): List<Any> {
            return ImmutableList.create(left).pushBack(right)
        }

        override fun leftInverse(result: List<Any>): List<Any>? {
            return if (rightInverse(result) == null) {
                null
            } else result.subList(0, result.size - 1)

        }

        override fun rightInverse(result: List<Any>): Any? {
            return if (result.isEmpty()) {
                null
            } else result[result.size - 1]

        }
    }
    private val comma = Recognizer.fromString(",", tokenizer, false)
    private val colon = Recognizer.fromString(":", tokenizer, false)
    private val stringsPrefix = Recognizer.fromString("S", tokenizer, false)
    private val intsPrefix = Recognizer.fromString("I", tokenizer, false)
    private val strings = stringsPrefix + comma.join1(id.fold(add))
    private val ints = intsPrefix + comma.join1(num.fold(add))
    private val parser = EmptyListCreator<Any>() + colon.join(strings or ints)
    private lateinit var input: ParserStream
    private var item: List<Any>? = null
    private var output: String? = null

    @Test
    fun testOneElementEach() {
        withInput("Sa:I1")
        actParse()
        actPrint()

        Assert.assertEquals("Sa:I1", output)
    }

    @Test
    fun testMultiple() {
        withInput("Sa:Sb:Sc")
        actParse()
        actPrint()

        Assert.assertEquals("Sa,b,c", output)
    }

    @Test
    fun testMultipleEach() {
        withInput("Sa:Sb,c:I1,2,3:I4:Sd,e:Sf:I5:I6")
        actParse()
        actPrint()

        Assert.assertEquals("Sa,b,c:I1,2,3,4:Sd,e,f:I5,6", output)
    }

    private fun actPrint() {
        output = parser.print(item!!)?.toString()
    }

    private fun actParse() {
        item = parser.parse(input)
    }

    private fun withInput(input: String) {
        this.input = input.createParserStream()

    }
}
