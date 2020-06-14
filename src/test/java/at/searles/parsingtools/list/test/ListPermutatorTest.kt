package at.searles.parsingtools.list.test

import at.searles.parsing.ParserStream
import at.searles.parsing.ParserStream.Companion.createParserStream
import at.searles.parsingtools.list.ListPermutator
import org.junit.Assert
import org.junit.Test

class ListPermutatorTest {

    @Test
    fun parse() {
        val mapping = ListPermutator<String>(1, 2, 0)

        val result = mapping.parse("".createParserStream(), listOf("A", "B", "C"))

        Assert.assertEquals(listOf("B", "C", "A"), result)
    }

    @Test
    fun left() {
        val mapping = ListPermutator<String>(1, 2, 0)

        val result = mapping.left(listOf("A", "B", "C"))

        Assert.assertEquals(listOf("C", "A", "B"), result)
    }
}