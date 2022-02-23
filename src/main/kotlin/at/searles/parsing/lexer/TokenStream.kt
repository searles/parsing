package at.searles.parsing.lexer

import at.searles.parsing.codepoint.CodePointStream
import at.searles.parsing.codepoint.Frame
import at.searles.parsing.codepoint.FrameStream
import at.searles.parsing.codepoint.IndexedStream
import at.searles.parsing.lexer.fsa.IntSet
import java.io.Reader

class TokenStream(val stream: FrameStream) {
    constructor(string: String): this(FrameStream(string))
    constructor(reader: Reader): this(FrameStream(reader))
    constructor(stream: CodePointStream): this(FrameStream(IndexedStream.of(stream)))

    private var tokenIds: IntSet? = null
    private var matchingLexer: Lexer? = null

    val startIndex get() = stream.startIndex
    val endIndex get() = stream.endIndex // inclusive

    fun getTokenIds(lexer: Lexer): IntSet? {
        if(matchingLexer == lexer) {
            return tokenIds
        }

        if(tokenIds != null) {
            stream.reset()
        }

        require(stream.isReset)

        tokenIds = lexer.selectNextToken(stream)
        matchingLexer = lexer

        return tokenIds
    }

    val frame: Frame get() = stream.frame

    fun next() {
        stream.next()
        matchingLexer = null
        tokenIds = null
    }

    fun restoreIndex(index: Long) {
        stream.reset(index)
        matchingLexer = null
        tokenIds = null
    }
}