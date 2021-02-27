package at.searles.parsing.parser.format

import at.searles.parsing.lexer.fsa.IntSet
import at.searles.parsing.parser.ParserStream
import at.searles.parsing.printer.StringOutStream
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.lang.StringBuilder

class FormatTest {
    abstract class Edit {
        abstract fun edit(sb: StringBuilder)
    }

    data class Delete(val index: Long, val length: Long): Edit() {
        override fun edit(sb: StringBuilder) {
            sb.delete(index.toInt(), (index + length).toInt())
        }
    }

    data class Insert(val index: Long, val s: String): Edit() {
        override fun edit(sb: StringBuilder) {
            sb.insert(index.toInt(), s)
        }
    }

    data class Highlight(val index: Long, val length: Long): Edit() {
        override fun edit(sb: StringBuilder) {
            // do nothing
        }
    }

    lateinit var edits: ArrayList<Edit>

    @Before
    fun setUp() {
        edits = ArrayList()
    }

    class MyState(val listSize: Int, stream: ParserStream): ParserStream.State(stream)

    inner class MyParserStream(src: String): ParserStream(src) {
        override fun createState(): State {
            return MyState(edits.size, this)
        }

        override fun restoreState(state: State) {
            super.restoreState(state)

            while(edits.size > (state as MyState).listSize) {
                edits.removeLast()
            }
        }
    }

    @Test
    fun testGenerateDeleteCommands() {
        val src = """  {  f  (  )  ;  }  """


        val stream = MyParserStream(src)

        stream.listener = object: ParserStream.Listener {
            override fun onSpecialToken(
                tokenIds: IntSet,
                frame: CharSequence,
                index: Long,
                length: Long
            ) {
                edits.add(Delete(index, length))
            }

            override fun onToken(tokenId: Int, frame: CharSequence, index: Long, length: Long) {}
            override fun onSelect(source: ParserStream, label: Any, startState: ParserStream.State) {}
            override fun onMark(source: ParserStream, label: Any) {}
        }

        val result = Rules.block.parse(stream)

        Assert.assertTrue(result.isSuccess)
        Assert.assertEquals(
            listOf(Delete(0L, 2L), Delete(3L, 2L), Delete(6L, 2L),
                Delete(9L, 2L), Delete(12L, 2L), Delete(15L, 2L)),
            edits
        )
    }

    @Test
    fun testCanFormatBlock() {
        val src = """{f();g();}"""

        val stream = MyParserStream(src)

        stream.listener = object: ParserStream.Listener {
            override fun onSpecialToken(
                tokenIds: IntSet,
                frame: CharSequence,
                index: Long,
                length: Long
            ) {
                // TODO keep comments
                edits.add(Delete(index, length))
            }

            override fun onToken(tokenId: Int, frame: CharSequence, index: Long, length: Long) {}

            override fun onSelect(source: ParserStream, label: Any, startState: ParserStream.State) {
                if(label == "indent") {
                    val iterator = edits.listIterator((startState as MyState).listSize)

                    while(iterator.hasNext()) {
                        val edit = iterator.next()

                        if(edit is Insert) {
                            if(edit.s.startsWith("\n")) {
                                iterator.set(
                                    Insert(edit.index, "\n    " + edit.s.substring(1))
                                )
                            }
                        }
                    }
                }
            }

            override fun onMark(source: ParserStream, label: Any) {
                if(label == "newLine") {
                    edits.add(Insert(source.index, "\n"))
                }
            }
        }

        val result = Rules.block.parse(stream)

        Assert.assertTrue(result.isSuccess)

        val sb = StringBuilder(src)

        for(cmd in edits.reversed()) {
            cmd.edit(sb)
        }

        Assert.assertEquals("{\n    f();\n    g();\n}", sb.toString())
    }

    @Test
    fun testNumbersHighlighted() {
        val src = """f(10, 20)"""

        val stream = MyParserStream(src)

        stream.listener = object: ParserStream.Listener {
            override fun onSpecialToken(tokenIds: IntSet, frame: CharSequence, index: Long, length: Long) {}

            override fun onToken(tokenId: Int, frame: CharSequence, index: Long, length: Long) {}
            override fun onSelect(source: ParserStream, label: Any, startState: ParserStream.State) {
                if(label == "num") {
                    val length = source.index - startState.index
                    edits.add(Highlight(startState.index, length))
                }
            }

            override fun onMark(source: ParserStream, label: Any) {
            }
        }

        val result = Rules.call.parse(stream)

        Assert.assertTrue(result.isSuccess)
        Assert.assertEquals(
            listOf(Highlight(2L, 2L), Highlight(6L, 2L)),
            edits
        )
    }
}