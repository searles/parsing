package at.searles.parsing.parser.tools

import at.searles.parsing.parser.Parser
import at.searles.parsing.parser.ParserStream
import at.searles.parsing.parser.tools.NewInstanceBuilders.newInstance
import at.searles.parsing.parser.tools.NewInstanceBuilders.plus
import org.junit.Assert
import org.junit.Test

class NewInstanceBuildersTest {
    @Test
    fun testCreateDataClassInstanceFromPair() {
        data class A(val s: String, val i: Int)

        val creator = newInstance<A>().from<Pair<String, Int>>()

        val result = creator.convert(Pair("Hello", 123))
        Assert.assertEquals(A("Hello", 123), result)
    }

    @Test
    fun testCreatePairFromDataClassInstance() {
        data class A(val s: String, val i: Int)

        val creator = newInstance<A>().from<Pair<String, Int>>()

        val result = creator.invert(A("Hello", 123))
        Assert.assertEquals(Pair("Hello", 123), result.value)
    }

    @Test
    fun testCreateDataClassInstanceFromTwoPair() {
        data class A(val s: String, val i: Int, val t: List<Int>)
        val a = A("Hello", 123, listOf(1, 2, 3))
        val pairs = Pair(Pair("Hello", 123), listOf(1, 2, 3))

        val creator = newInstance<A>().from<Pair<Pair<String, Int>, List<Int>>>()

        val result = creator.convert(pairs)
        Assert.assertEquals(a, result)
    }

    @Test
    fun testCreateTwoPairFromDataClassInstance() {
        data class A(val s: String, val i: Int, val t: List<Int>)
        val a = A("Hello", 123, listOf(1, 2, 3))
        val pairs = Pair(Pair("Hello", 123), listOf(1, 2, 3))

        val creator = newInstance<A>().from<Pair<Pair<String, Int>, List<Int>>>()

        val result = creator.invert(a)
        Assert.assertEquals(pairs, result.value)
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
        data class A(val i: Int = 0)
        data class B(val i: Int = 0)
        class C(val a: A, val b: B)

        val parser = InitValue(B()) + newInstance<C>(A())

        Assert.assertTrue(parser.parse(ParserStream("")).isSuccess)
    }

    @Test
    fun testNewInstancePrintWithPresetArgs() {
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

    @ExperimentalStdlibApi
    @Test
    fun testNewInstanceFoldFromRecognizer() {
        data class A(val i: Int)

        val parser = InitValue(1) + (Mark("just_a_recognizer") + newInstance<A>().left())

        Assert.assertTrue(parser.parse(ParserStream("")).isSuccess)
    }
}