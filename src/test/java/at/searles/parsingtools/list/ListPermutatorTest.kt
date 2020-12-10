package at.searles.parsingtools.list

import at.searles.parsing.ParserStream
import org.junit.Assert
import org.junit.Test

class ListPermutatorTest {

    @Test
    fun parse() {
        val mapping = ListPermutator<String>(1, 2, 0)

        val result = mapping.reduce(listOf("A", "B", "C"), ParserStream.create(""))

        Assert.assertEquals(listOf("B", "C", "A"), result)
    }

    @Test
    fun left() {
        val mapping = ListPermutator<String>(1, 2, 0)

        val result = mapping.left(listOf("A", "B", "C"))

        Assert.assertEquals(listOf("C", "A", "B"), result)
    }
}