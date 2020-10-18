package at.searles.parsingtools

import at.searles.parsing.Mapping.Companion.cast
import at.searles.parsing.ParserStream
import org.junit.Assert
import org.junit.Test

class CastTest {
    @Test
    fun castTest() {
        val emptyStream = ParserStream.create("")

        open class A {}
        class B: A() {}
        class C: A() {}

        val a = A()
        val b = B()
        val c = C()

        val m = cast<B, A>()

        Assert.assertTrue(m.parse(emptyStream, b) == b)
        Assert.assertTrue(m.left(b) == b)
        Assert.assertNull(m.left(c))
    }
}