package at.searles.parsing.parser

import at.searles.parsing.codepoint.BufferedStream
import at.searles.parsing.codepoint.CodePointStream
import at.searles.parsing.lexer.FrameStream
import at.searles.parsing.lexer.Lexer
import at.searles.parsing.lexer.Token
import at.searles.parsing.lexer.fsa.IntSet
import java.io.Reader

/**
 * This class is open for decorators
 */
open class ParserStream(private val stream: FrameStream) {
    constructor(string: String): this(FrameStream(string))
    constructor(reader: Reader): this(FrameStream(reader))
    constructor(stream: CodePointStream): this(FrameStream(BufferedStream.of(stream)))

    private var lexer: Lexer? = null
    private var tokenIds: IntSet? = null
    private var isTokenAccepted = false

    var listener: Listener? = null

    val index: Long get() {
        return if(isTokenAccepted) {
            stream.frameIndex + stream.frameLength
        } else {
            stream.frameIndex
        }
    }

    fun parseToken(token: Token): ParserResult<CharSequence> {
        while(true) {
            consumeTokenIfAccepted()
            readNewTokenIfNecessary(token)

            if(isTokenSpecial()) {
                listener?.onSpecialToken(tokenIds!!, stream.getFrame(), stream.frameIndex, stream.frameLength)
                isTokenAccepted = true
                continue
            }

            return if(isTokenMatching(token.tokenId)) {
                listener?.onToken(token.tokenId, stream.getFrame(), stream.frameIndex, stream.frameLength)
                isTokenAccepted = true
                ParserResult.of(stream.getFrame(), stream.frameIndex, stream.frameLength)
            } else {
                ParserResult.failure
            }
        }
    }

    open fun notifyToken(tokenId: Int, frame: CharSequence, frameIndex: Long, frameLength: Long) {
    }

    private fun readNewTokenIfNecessary(token: Token) {
        if (lexer != token.lexer) {
            stream.resetFrame()
            lexer = token.lexer
            tokenIds = token.lexer.readNextToken(stream)
        }
    }

    private fun consumeTokenIfAccepted() {
        if (isTokenAccepted) {
            stream.consumeFrame()
            resetTokenFields()
        }
    }

    open class State(stream: ParserStream) {
        val index = stream.index
    }

    open fun createState(): State {
        return State(this)
    }

    open fun restoreState(state: State) {
        if(state.index != stream.frameIndex) {
            stream.restoreIndex(state.index)
            resetTokenFields()
        }
    }

    private fun resetTokenFields() {
        lexer = null
        tokenIds = null
        isTokenAccepted = false
    }

    private fun isTokenMatching(tokenId: Int): Boolean {
        return tokenIds?.contains(tokenId) == true
    }

    private fun isTokenSpecial(): Boolean {
        return tokenIds != null && lexer!!.isSpecialToken(tokenIds!!)
    }

    fun notifySelection(label: Any, startState: State) {
        listener?.onSelect(this, label, startState)
    }

    fun notifyMark(label: Any) {
        listener?.onMark(this, label)
    }

    interface Listener {
        fun onSpecialToken(tokenIds: IntSet, frame: CharSequence, index: Long, length: Long)
        fun onToken(tokenId: Int, frame: CharSequence, index: Long, length: Long)
        fun onSelect(source: ParserStream, label: Any, startState: State)
        fun onMark(source: ParserStream, label: Any)

    }
}