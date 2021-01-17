package at.searles.parsing.parser

import at.searles.parsing.lexer.Frame
import at.searles.parsing.lexer.FrameStream
import at.searles.parsing.lexer.Lexer
import at.searles.parsing.lexer.Token
import at.searles.parsing.lexer.fsa.IntSet
import java.io.Reader

class ParserStream(private val stream: FrameStream) {
    constructor(string: String): this(FrameStream(string))
    constructor(reader: Reader): this(FrameStream(reader))

    private var lexerForFrame: Lexer? = null
    private var tokenIdsOfFrame: IntSet? = null
    private var isTokenAccepted = false

    fun acceptToken(token: Token): ParserResult<Frame> {
        if(isTokenAccepted) {
            consumeToken()
        }

        if(lexerForFrame != token.lexer) {
            // TODO Maybe refactor...
            readTokenFromNewLexer(token.lexer)
        }

        if(!isTokenMatching(token.tokenId)) {
            return createFail()
        }

        isTokenAccepted = true
        return createSuccess(stream.frame)
    }

//    fun resetReadIndex(newIndex: Int) {
//        if(newIndex != stream.index) {
//            stream.backtrackToIndex(index)
//            resetTokenFields()
//        }
//    }

    private fun consumeToken() {
        stream.consumeFrame()
        resetTokenFields()
    }

    private fun resetTokenFields() {
        lexerForFrame = null
        tokenIdsOfFrame = null
        isTokenAccepted = false
    }

    private fun isTokenMatching(tokenId: Int): Boolean {
        return tokenIdsOfFrame?.contains(tokenId) == true
    }

    private fun readTokenFromNewLexer(newLexer: Lexer) {
        stream.resetFrame()
        lexerForFrame = newLexer
        tokenIdsOfFrame = newLexer.readNextToken(stream) // TODO here should be skipped tokens
    }

    private class InnerSuccess: Success<Any?> {
        override val value: Any? get() = mValue
        var mValue: Any? = null
    }

    private val success = InnerSuccess()
    private val fail = object: Fail<Any> {}

    fun <A> createSuccess(value: A): Success<A> {
        success.mValue = value
        @Suppress("UNCHECKED_CAST")
        return success as Success<A>
    }

    fun <A> createFail(): Fail<A> {
        @Suppress("UNCHECKED_CAST")
        return fail as Fail<A>
    }
}