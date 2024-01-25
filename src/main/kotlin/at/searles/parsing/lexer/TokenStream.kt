package at.searles.parsing.lexer

import at.searles.parsing.codepoint.CodePointStream
import at.searles.parsing.codepoint.Frame
import at.searles.parsing.codepoint.FrameStream
import at.searles.parsing.codepoint.IndexedStream
import at.searles.parsing.lexer.fsa.IntSet
import java.io.Reader

open class TokenStream(val stream: FrameStream) {
    constructor(string: String): this(FrameStream(string))
    constructor(reader: Reader): this(FrameStream(reader))
    constructor(stream: CodePointStream): this(FrameStream(IndexedStream.of(stream)))

    // The currently matched token is defined by the used lexer and
    // if there is a match by the ids of the matching tokens.
    // If matchingLexer is not null but tokenIds is, then there is no match.
    private var tokenIds: IntSet? = null
    private var matchingLexer: Lexer? = null

    val startIndex get() = stream.startIndex
    val endIndex get() = stream.endIndex // inclusive

    val frame: Frame get() = stream.frame

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

    fun next() {
        stream.next()
        matchingLexer = null
        tokenIds = null
    }

    fun getState(): State {
        return State(startIndex)
    }

    fun restoreState(state: State) {
        stream.reset(state.index)
        matchingLexer = null
        tokenIds = null
    }

    open class State(val index: Long)
}