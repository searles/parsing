package at.searles.parsingtools.formatter

import at.searles.buf.Frame
import at.searles.lexer.TokenStream
import at.searles.parsing.BacktrackNotAllowedException
import at.searles.parsing.ParserStream
import at.searles.parsing.Recognizable

open class CodeFormatter(private val whiteSpaceTokenId: Int, private val parser: Recognizable) {

    var indentation = "    "
    var newline = "\n"
    var space = " "

    private val indentAnnotations = HashSet<String>()
    private val forceNewLineAnnotations = HashSet<String>()
    private val forceSpaceAnnotations = HashSet<String>()

    fun format(editableText: EditableText): Long {
        val formatterInstance = FormatterInstance(editableText)
        val stream = ParserStream.create(editableText)

        stream.tokStream().setListener(formatterInstance)
        stream.listener = formatterInstance

        try {
            parser.recognize(stream)
            formatterInstance.addWhiteSpaceInsertCommand(true) // for terminating \n etc...
        } catch(e: BacktrackNotAllowedException) {
            formatterInstance.addWhiteSpaceInsertCommand() // for terminating \n etc...
            // ignore
        }


        formatterInstance.changeRunnables.reversed().forEach { it.run() }

        return formatterInstance.position
    }

    fun addIndentLabel(label: String) {
        this.indentAnnotations.add(label)
    }

    fun addForceSpaceLabel(label: String) {
        this.forceSpaceAnnotations.add(label)
    }

    fun addForceNewlineLabel(label: String) {
        this.forceNewLineAnnotations.add(label)
    }

    private inner class FormatterInstance(val editableText: EditableText): TokenStream.Listener, ParserStream.Listener {
        private var indentLevel = 0

        private var forceSpace = false
        private var forceNewLine = false
        private var forceEmptyLine = false

        val changeRunnables = ArrayList<Runnable>()

        var position: Long = 0
            private set

        fun addWhiteSpaceInsertCommand(ignoreSpace: Boolean = false) {
            if(forceEmptyLine) {
                changeRunnables.add(InsertCommand(editableText, position, newline.repeat(2) + indentation.repeat(indentLevel)))
            } else if(forceNewLine) {
                changeRunnables.add(InsertCommand(editableText, position, newline + indentation.repeat(indentLevel)))
            } else if(forceSpace && !ignoreSpace) {
                changeRunnables.add(InsertCommand(editableText, position, space))
            }

            forceSpace = false
            forceNewLine = false
            forceEmptyLine = false
        }

        private fun addDeleteWhiteSpaceCommand(frame: Frame) {
            when(countNewlines(frame)) {
                0 -> forceSpace = true
                1 -> forceNewLine = true
                else -> forceEmptyLine = true
            }

            changeRunnables.add(DeleteCommand(editableText, frame.start, frame.end))
        }

        fun indent() {
            indentLevel++
        }

        fun unindent() {
            indentLevel--
        }

        private fun countNewlines(chs: CharSequence): Int {
            return chs.count { it == '\n' }
        }

        override fun tokenConsumed(src: TokenStream, tokenId: Int, frame: Frame) {
            require(position == frame.start)

            if (tokenId == whiteSpaceTokenId) {
                addDeleteWhiteSpaceCommand(frame)
            } else {
                addWhiteSpaceInsertCommand()
            }

            position = frame.end
        }

        override fun onRefStart(parserStream: ParserStream, label: String) {
            if (indentAnnotations.contains(label)) {
                indent()
            }
        }

        override fun onRefSuccess(parserStream: ParserStream, label: String) {
            if (indentAnnotations.contains(label)) {
                unindent()
            }

            if (forceSpaceAnnotations.contains(label)) {
                forceSpace = true
            }

            if (forceNewLineAnnotations.contains(label)) {
                forceNewLine = true
            }
        }

        override fun onRefFail(parserStream: ParserStream, label: String) {
            if (indentAnnotations.contains(label)) {
                unindent()
            }
        }
    }

    private class InsertCommand(val editableText: EditableText, val position: Long, val insert: String): Runnable {
        override fun run() {
            editableText.insert(position, insert)
        }
    }

    private class DeleteCommand(val editableText: EditableText, val start: Long, val end: Long): Runnable {
        override fun run() {
            editableText.delete(start, end)
        }
    }
}