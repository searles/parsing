package at.searles.parsing.parser.tools

import org.junit.Assert
import org.junit.Test

class BuilderParserMappingTest {
    @Test
    fun testCast() {
        open class A {}
        class B: A() {}

        val cast = ReducerBuilders.cast<A>().from<B>()

        val a: A = cast.convert(B())
        val b = cast.print(a)

        Assert.assertTrue(b.isSuccess)
    }

    @Test
    fun testNullable() {
        class A

        val nullableCast = ReducerBuilders.nullable<A>()

        Assert.assertFalse(nullableCast.print(null).isSuccess)
    }
}