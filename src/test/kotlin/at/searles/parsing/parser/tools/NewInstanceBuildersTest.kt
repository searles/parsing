package at.searles.parsing.parser.tools

import at.searles.parsing.parser.Parser
import at.searles.parsing.lexer.TokenStream
import at.searles.parsing.parser.Recognizer
import at.searles.parsing.parser.RecognizerResult
import at.searles.parsing.parser.combinators.TokenRecognizer
import at.searles.parsing.parser.tools.reflection.NewInstanceBuilders.newInstance
import at.searles.parsing.parser.tools.reflection.NewInstanceBuilders.plus
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

        val parser = InitValue(A()) + (InitValue(B()).asParser() + newInstance<C>().left<A>())

        Assert.assertTrue(parser.parse(TokenStream("")).isSuccess)
    }

    @ExperimentalStdlibApi
    @Test
    fun testNewInstanceFoldPrint() {
        data class A(val i: Int = 0)
        data class B(val i: Int = 0)
        data class C(val a: A, val b: B)

        val parser = InitValue(A()) + ( InitValue(B()).asParser() + newInstance<C>().left())

        Assert.assertTrue(parser.print(C(A(), B())).isSuccess)
    }

    @ExperimentalStdlibApi
    @Test
    fun testNewInstanceFoldFromLeftPair() {
        data class A(val i: Int = 0)
        data class B(val i: Int = 0)
        data class C(val i: Int = 0)
        class D(val a: A, val b: B, val c: C)

        val parser = InitValue(A()).asParser() + InitValue(B()).asParser() + (InitValue(C()).asParser() + newInstance<D>().left())

        Assert.assertTrue(parser.parse(TokenStream("")).isSuccess)
    }

    @ExperimentalStdlibApi
    @Test
    fun testNewInstanceFoldPrintFromLeftPair() {
        data class A(val i: Int = 0)
        data class B(val i: Int = 0)
        data class C(val i: Int = 0)
        class D(val a: A, val b: B, val c: C)

        val parser = InitValue(A()).asParser() + InitValue(B()).asParser() + (InitValue(C()).asParser() + newInstance<D>().left())

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

        val parser: Parser<E> = InitValue(A()).asParser() + InitValue(B()).asParser() + InitValue(C()).asParser() + (InitValue(D()).asParser() + newInstance<E>().left<Pair<Pair<A, B>, C>>())

        Assert.assertTrue(parser.parse(TokenStream("")).isSuccess)
    }

    @ExperimentalStdlibApi
    @Test
    fun testNewInstanceFoldPrintFromLeftPairs() {
        data class A(val i: Int = 0)
        data class B(val i: Int = 0)
        data class C(val i: Int = 0)
        data class D(val i: Int = 0)
        class E(val a: A, val b: B, val c: C, val d: D)

        val parser: Parser<E> = InitValue(A()).asParser() + InitValue(B()).asParser() + InitValue(C()).asParser() + (InitValue(D()).asParser() + newInstance<E>().left<Pair<Pair<A, B>, C>>())

        Assert.assertTrue(parser.print(E(A(), B(), C(), D())).isSuccess)
    }

    @Test
    fun testNewInstanceWithPresetArgs() {
        data class A(val i: Int = 0)
        data class B(val i: Int = 0)
        class C(val a: A, val b: B)

        val parser = InitValue(B()).asParser() + newInstance<C>(A())

        Assert.assertTrue(parser.parse(TokenStream("")).isSuccess)
    }

    @Test
    fun testNewInstancePrintWithPresetArgs() {
        data class A(val i: Int = 0)
        data class B(val i: Int = 0)
        class C(val a: A, val b: B)

        val parser = InitValue(B()).asParser() + newInstance<C>(A())

        Assert.assertTrue(parser.print(C(A(), B())).isSuccess)
    }


    @ExperimentalStdlibApi
    @Test
    fun testNewInstanceFoldWithPresets() {
        data class A(val i: Int = 0)
        data class B(val i: Int = 0)
        data class C(val i: Int = 0)
        class D(val a: A, val b: B, val c: C)

        val parser = InitValue(B()) + (InitValue(C()).asParser() + newInstance<D>(A()).left())

        Assert.assertTrue(parser.parse(TokenStream("")).isSuccess)
    }

    @ExperimentalStdlibApi
    @Test
    fun testNewInstanceFoldPrintWithPresets() {
        data class A(val i: Int = 0)
        data class B(val i: Int = 0)
        data class C(val i: Int = 0)
        class D(val a: A, val b: B, val c: C)

        val parser = InitValue(B()) + (InitValue(C()).asParser() + newInstance<D>(A()).left())

        Assert.assertTrue(parser.print(D(A(), B(), C())).isSuccess)
    }

    @ExperimentalStdlibApi
    @Test
    fun testNewInstanceFoldFromRecognizer() {
        data class A(val i: Int)

        val parser = InitValue(1) + (Recognizer { stream -> RecognizerResult.of(stream.startIndex, 0) } + newInstance<A>().left())

        Assert.assertTrue(parser.parse(TokenStream("")).isSuccess)
    }

    @ExperimentalStdlibApi
    @Test
    fun testNewInstanceAfterFold() {
        class TestClass(val list: List<Int>)

        val parser: Parser<TestClass> = InitValue(1).asParser() + (InitValue(2).asParser() + BinaryList() + newInstance())

        val result = parser.parse("")

        Assert.assertTrue(result.isSuccess)
        Assert.assertEquals(listOf(1, 2), result.value.list)

        Assert.assertFalse(parser.print(TestClass(emptyList())).isSuccess)
        Assert.assertTrue(parser.print(TestClass(listOf(1, 2))).isSuccess)
    }
}