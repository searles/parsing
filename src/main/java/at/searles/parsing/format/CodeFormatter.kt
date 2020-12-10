package at.searles.parsing.format

import at.searles.buf.Frame
import at.searles.parsing.CanRecognize
import at.searles.parsing.ParserStream

open class CodeFormatter(private val parser: CanRecognize, private val whiteSpaceTokenId: Int) {

    fun format(editableText: EditableText): Long {
        val innerFormatter = InnerFormatter()
        val isSuccessful = parser.recognize(ParserStream.create(editableText).apply { this.listener = innerFormatter })
        
        val commandList = if(isSuccessful) {
            innerFormatter.commandList
        } else {
            innerFormatter.maxCommandList
        }
        
        val formatContext = EditableFormatContext(editableText)
        
        commandList.forEach { 
            it.applyTo(formatContext) 
        }
        
        return innerFormatter.maxOffset
    }

    interface Cmd {
        fun applyTo(formatContext: EditableFormatContext)
    }
    
    class Indent(): Cmd {
        override fun applyTo(formatContext: EditableFormatContext) {
            formatContext.indent()
        }
    }
    
    class Unindent(): Cmd {
        override fun applyTo(formatContext: EditableFormatContext) {
            formatContext.unindent()
        }
    }
    
    class NewLine(private val replaceFrom: Long, private val replaceLength: Int): Cmd {
        override fun applyTo(formatContext: EditableFormatContext) {
            formatContext.delete(replaceFrom, replaceLength)
            formatContext.appendNewLine()
        }
    }
    
    class EmptyLine(private val replaceFrom: Long, private val replaceLength: Int): Cmd {
        override fun applyTo(formatContext: EditableFormatContext) {
            formatContext.delete(replaceFrom, replaceLength)
            formatContext.appendEmptyLine()
        }
    }
    
    class Space(private val replaceFrom: Long, private val replaceLength: Int): Cmd {
        override fun applyTo(formatContext: EditableFormatContext) {
            formatContext.delete(replaceFrom, replaceLength)
            formatContext.appendSpace()
        }
    }
    
    class TokenCommand(private val tokenId: Int, private val start: Long, private val length: Int) : Cmd {
        override fun applyTo(formatContext: EditableFormatContext) {
            formatContext.confirmToken(tokenId, start, length)
        }
    }

    object EmptyCommand: Cmd {
        override fun applyTo(formatContext: EditableFormatContext) {
            // ignore
        }
    }

    inner class InnerFormatter(): Formatter<Cmd>() {
        override fun createMarkCommand(marker: Any, offset: Long): Cmd {
            return when(marker) {
                Markers.Indent -> Indent()
                Markers.Unindent -> Unindent()
                Markers.NewLine -> NewLine(offset, 0)
                Markers.EmptyLine -> EmptyLine(offset, 0)
                Markers.Space -> Space(offset, 0)
                else -> EmptyCommand
            }
        }

        override fun createTokenCommand(tokenId: Int, frame: Frame): Cmd {
            if(tokenId != whiteSpaceTokenId) {
                return TokenCommand(tokenId, frame.start, frame.length)
            }

            return when (frame.count { it == '\n' }) {
                0 -> Space(frame.start, frame.length)
                1 -> NewLine(frame.start, frame.length)
                else -> EmptyLine(frame.start, frame.length)
            }
        }
    }
}