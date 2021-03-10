package at.searles.parsing.parser.tools

import at.searles.parsing.parser.Parser
import at.searles.parsing.parser.ParserStream
import at.searles.parsing.parser.tools.ReducerBuilders.newInstance
import at.searles.parsing.parser.tools.ReducerBuilders.plus
import org.junit.Assert
import org.junit.Test
import kotlin.reflect.KType
import kotlin.reflect.typeOf

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

    @ExperimentalStdlibApi
    @Test
    fun testNewInstanceFold() {
        data class A(val i: Int = 0)
        data class B(val i: Int = 0)
        class C(val a: A, val b: B)

        val parser = InitValue(A()) + ( InitValue(B()) + newInstance<C>().left<A>())

        Assert.assertTrue(parser.parse(ParserStream("")).isSuccess)
    }

    @ExperimentalStdlibApi
    @Test
    fun testNewInstanceFoldPrint() {
        data class A(val i: Int = 0)
        data class B(val i: Int = 0)
        data class C(val a: A, val b: B)

        val parser = InitValue(A()) + ( InitValue(B()) + newInstance<C>().left())

        Assert.assertTrue(parser.print(C(A(), B())).isSuccess)
    }

    @ExperimentalStdlibApi
    @Test
    fun testNewInstanceFoldFromLeftPair() {
        data class A(val i: Int = 0)
        data class B(val i: Int = 0)
        data class C(val i: Int = 0)
        class D(val a: A, val b: B, val c: C)

        val parser = InitValue(A()) + InitValue(B()) + (InitValue(C()) + newInstance<D>().left())

        Assert.assertTrue(parser.parse(ParserStream("")).isSuccess)
    }

    @ExperimentalStdlibApi
    @Test
    fun testNewInstanceFoldPrintFromLeftPair() {
        data class A(val i: Int = 0)
        data class B(val i: Int = 0)
        data class C(val i: Int = 0)
        class D(val a: A, val b: B, val c: C)

        val parser = InitValue(A()) + InitValue(B()) + (InitValue(C()) + newInstance<D>().left())

        Assert.assertTrue(parser.print(D(A(), B(), C())).isSuccess)
    }

    @ExperimentalStdlibApi
    @Test
    fun testNewInstanceFoldFromLeftPairs() {
        data class A(val i: Int = 0)
        data class B(val i: Int = 0)
        data class C(val i: Int = 0)
        data class D(val i: Int = 0)
        class E(val a: A, val b: B, val c: C, val d: D)

        val parser: Parser<E> = InitValue(A()) + InitValue(B()) + InitValue(C()) + (InitValue(D()) + newInstance<E>().left<Pair<Pair<A, B>, C>>())

        Assert.assertTrue(parser.parse(ParserStream("")).isSuccess)
    }

    @ExperimentalStdlibApi
    @Test
    fun testNewInstanceFoldPrintFromLeftPairs() {
        data class A(val i: Int = 0)
        data class B(val i: Int = 0)
        data class C(val i: Int = 0)
        data class D(val i: Int = 0)
        class E(val a: A, val b: B, val c: C, val d: D)

        val parser: Parser<E> = InitValue(A()) + InitValue(B()) + InitValue(C()) + (InitValue(D()) + newInstance<E>().left<Pair<Pair<A, B>, C>>())

        Assert.assertTrue(parser.print(E(A(), B(), C(), D())).isSuccess)
    }

    @Test
    fun testNewInstanceWithPresetArgs() {
        // TODO must test pairs too.

        data class A(val i: Int = 0)
        data class B(val i: Int = 0)
        class C(val a: A, val b: B)

        val parser = InitValue(B()) + newInstance<C>(A())

        Assert.assertTrue(parser.parse(ParserStream("")).isSuccess)
    }

    @Test
    fun testNewInstancePrintWithPresetArgs() {
        // TODO must test pairs too.

        data class A(val i: Int = 0)
        data class B(val i: Int = 0)
        class C(val a: A, val b: B)

        val parser = InitValue(B()) + newInstance<C>(A())

        Assert.assertTrue(parser.print(C(A(), B())).isSuccess)
    }


    @ExperimentalStdlibApi
    @Test
    fun testNewInstanceFoldWithPresets() {
        data class A(val i: Int = 0)
        data class B(val i: Int = 0)
        data class C(val i: Int = 0)
        class D(val a: A, val b: B, val c: C)

        val parser = InitValue(B()) + (InitValue(C()) + newInstance<D>(A()).left())

        Assert.assertTrue(parser.parse(ParserStream("")).isSuccess)
    }

    @ExperimentalStdlibApi
    @Test
    fun testNewInstanceFoldPrintWithPresets() {
        data class A(val i: Int = 0)
        data class B(val i: Int = 0)
        data class C(val i: Int = 0)
        class D(val a: A, val b: B, val c: C)

        val parser = InitValue(B()) + (InitValue(C()) + newInstance<D>(A()).left())

        Assert.assertTrue(parser.print(D(A(), B(), C())).isSuccess)
    }
}