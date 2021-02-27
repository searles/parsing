package at.searles.parsing.parser.format
import at.searles.parsing.printer.StringOutStream
import org.junit.Assert
import org.junit.Test

class PrintFormatTest {
    @Test
    fun testCanPrintBlocksWithItems() {
        val result = Rules.block.print(Block(listOf(Call("f", emptyList()))))

        val output = StringOutStream().run {
            result.print(IndentOutStream(this))
            this.toString()
        }

        Assert.assertEquals("{\n    f();\n}", output)
    }

    @Test
    fun testCanPrintBlocksWithNestedItems() {
        val result = Rules.block.print(
            Block(listOf(
                Call("f", emptyList()),
                Block(listOf(
                    Call("f", emptyList())
                ))
            ))
        )

        val output = StringOutStream().run {
            result.print(IndentOutStream(this))
            this.toString()
        }

        Assert.assertEquals("""{
            |    f();
            |    {
            |        f();
            |    }
            |}""".trimMargin(), output)
    }
}