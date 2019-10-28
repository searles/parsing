package at.searles.parsing.test

import at.searles.lexer.Lexer
import at.searles.lexer.SkipTokenizer
import at.searles.parsing.Parser
import at.searles.parsing.ParserStream
import at.searles.parsing.combinators.ParserAndParser
import at.searles.parsing.utils.common.ToInt
import at.searles.regex.Regex
import at.searles.regex.RegexParser
import org.junit.Assert
import org.junit.Test

class AndTest {
    @Test
    fun testAndParser() {
        // parser is
        // num: [0-9]+
        // pair1: num num >> add
        // pair2: num num >> mul
        // comb: pair1 & pair2
        val lexer = SkipTokenizer(Lexer())
        lexer.addSkipped(lexer.add(Regex.text(" ")))

        val num: Parser<Int> = Parser.fromRegex(RegexParser.parse("[0-9]+"), lexer, false, ToInt())

        val addPair = num.then(num.fold<Int, Int> { _, left, right -> left + right}, true)
        val mulPair = num.then(num.fold<Int, Int> { _, left, right -> left * right}, true)

        val combination = ParserAndParser<Int>(addPair, mulPair) { _, left, right -> left + 3 * right}

        val stream = ParserStream.fromString("2 5")

        val result = combination.parse(stream)

        Assert.assertEquals(37, result)
        Assert.assertEquals(0, stream.getStart())
        Assert.assertEquals(3, stream.getEnd())
    }
}
