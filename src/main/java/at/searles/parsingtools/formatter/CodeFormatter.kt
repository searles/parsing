package at.searles.parsingtools.formatter

import at.searles.buf.Frame
import at.searles.lexer.TokenStream
import at.searles.parsing.BacktrackNotAllowedException
import at.searles.parsing.ParserStream
import at.searles.parsing.Recognizable
import at.searles.parsing.format.CodeFormatContext
import at.searles.parsing.format.FormatRules
import at.searles.parsing.format.Printer
import java.util.*
import kotlin.collections.ArrayList

open class CodeFormatter(val rules: FormatRules, private val parser: Recognizable, private val whiteSpaceTokenId: Int) {

    fun format(editableText: EditableText): Long {
        return FormatterInstance(editableText).format()
    }

    private inner class FormatterInstance(val editableText: EditableText) : FormatListener, Printer {

        var context = CodeFormatContext(rules, this)
        val stash = Stack<CodeFormatContext>()
        val commands = ArrayList<Command>()

        var position: Long = 0

        fun format(): Long {
            val stream = ParserStream.create(editableText).apply {
                listener = this@FormatterInstance
                tokenStream.listener = this@FormatterInstance
            }

            try {
                parser.recognize(stream)
                context.insertNewLine()
            } catch(e: BacktrackNotAllowedException) {
                // ignore
            } finally {
                addCurrentFormattingCommands()
            }

            commands.reversed().forEach {
                it.run()
            }

            return position
        }

        override fun onFormat(marker: Any, parserStream: ParserStream) {
            context.format(marker)
        }

        override fun tokenConsumed(src: TokenStream, tokenId: Int, frame: Frame) {
            position = frame.start

            removeCommandsPastPosition(position)

            if(tokenId != whiteSpaceTokenId) {
                addCurrentFormattingCommands()
                position = frame.end
                return
            }

            replaceFormatting(frame)
            position = frame.end
        }

        private fun removeCommandsPastPosition(position: Long) {
            val iterator = commands.listIterator(commands.size)

            while(iterator.hasPrevious() && position < iterator.previous().position) {
                iterator.remove()
            }
        }

        fun addCurrentFormattingCommands() {
            context.applyFormatting()
        }

        private fun replaceFormatting(frame: Frame) {
            when(countNewlines(frame)) {
                0 -> context.insertSpace()
                1 -> context.insertNewLine()
                else -> context.insertEmptyLine()
            }

            remove(frame)
        }

        private fun countNewlines(chs: CharSequence): Int {
            return chs.count { it == '\n' }
        }

        private fun remove(frame: Frame) {
            commands.add(DeleteCommand(editableText, frame.start, frame.length.toLong()))
        }

        override fun print(seq: CharSequence) {
            commands.add(InsertCommand(editableText, position, seq.toString()))
        }
    }

    abstract class Command(val position: Long): Runnable

    private class InsertCommand(val editableText: EditableText, position: Long, val insert: String): Command(position) {
        override fun run() {
            editableText.insert(position, insert)
        }
    }

    private class DeleteCommand(val editableText: EditableText, position: Long, val length: Long): Command(position) {
        override fun run() {
            editableText.delete(position, length)
        }
    }
}