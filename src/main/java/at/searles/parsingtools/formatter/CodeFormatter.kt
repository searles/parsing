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

    private val indentAnnotations = HashSet<Any?>()
    private val forceNewLineAnnotations = HashSet<Any?>()
    private val forceSpaceAnnotations = HashSet<Any?>()

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

    fun addIndentAnnotation(annotation: Any?) {
        this.indentAnnotations.add(annotation)
    }

    fun addForceSpaceAnnotation(annotation: Any?) {
        this.forceSpaceAnnotations.add(annotation)
    }

    fun addForceNewlineAnnotation(annotation: Any?) {
        this.forceNewLineAnnotations.add(annotation)
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

            changeRunnables.add(DeleteCommand(editableText, frame.startPosition(), frame.endPosition()))
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
            require(position == frame.startPosition())

            if (tokenId == whiteSpaceTokenId) {
                addDeleteWhiteSpaceCommand(frame)
            } else {
                addWhiteSpaceInsertCommand()
            }

            position = frame.endPosition()
        }

        override fun <C> annotationBegin(parserStream: ParserStream, annotation: C) {
            if (indentAnnotations.contains(annotation)) {
                indent()
            }
        }

        override fun <C> annotationEndSuccess(parserStream: ParserStream, annotation: C) {
            if (indentAnnotations.contains(annotation)) {
                unindent()
            }

            if (forceSpaceAnnotations.contains(annotation)) {
                forceSpace = true
            }

            if (forceNewLineAnnotations.contains(annotation)) {
                forceNewLine = true
            }
        }

        override fun <C> annotationEndFail(parserStream: ParserStream, annotation: C) {
            if (indentAnnotations.contains(annotation)) {
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