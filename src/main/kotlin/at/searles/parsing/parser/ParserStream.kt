package at.searles.parsing.parser

import at.searles.parsing.codepoint.BufferedStream
import at.searles.parsing.codepoint.CodePointStream
import at.searles.parsing.lexer.FrameStream
import at.searles.parsing.lexer.Lexer
import at.searles.parsing.lexer.Token
import at.searles.parsing.lexer.fsa.IntSet
import java.io.Reader

class ParserStream(private val stream: FrameStream) {
    constructor(string: String): this(FrameStream(string))
    constructor(reader: Reader): this(FrameStream(reader))
    constructor(stream: CodePointStream): this(FrameStream(BufferedStream.of(stream)))

    private var lexerForFrame: Lexer? = null
    private var tokenIdsOfFrame: IntSet? = null
    private var isTokenAccepted = false

    val index: Long get() {
        return if(isTokenAccepted) {
            stream.frameIndex + stream.frameLength
        } else {
            stream.frameIndex
        }
    }

    fun acceptToken(token: Token): ParserResult<CharSequence> {
        if(isTokenAccepted) {
            consumeToken()
        }

        if(lexerForFrame != token.lexer) {
            readTokenFromNewLexer(token.lexer)
        }

        if(!isTokenMatching(token.tokenId)) {
            return ParserResult.failure
        }

        isTokenAccepted = true

        return ParserResult.of(stream.getFrame(), stream.frameIndex, stream.frameLength)
    }

    fun backtrackToIndex(newIndex: Long) {
        if(newIndex != stream.frameIndex) {
            stream.backtrackToIndex(newIndex)
            resetTokenFields()
        }
    }

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
        tokenIdsOfFrame = readTokensFromNewLexerSkipSpecialTokens(newLexer)
    }

    private fun readTokensFromNewLexerSkipSpecialTokens(newLexer: Lexer): IntSet? {
        while(true) {
            val tokenIds = newLexer.readNextToken(stream) ?: return null

            if(!newLexer.isSpecialToken(tokenIds)) {
                return tokenIds
            }

            consumeToken()
        }
    }
}