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

    val index: Long get() {
        // TODO: This can be done better.
        return if(isTokenAccepted) {
            stream.frame.index + stream.frame.length
        } else {
            stream.frame.index
        }
    }

    fun acceptToken(token: Token): ParserResult<Frame> {
        if(isTokenAccepted) {
            consumeToken()
        }

        if(lexerForFrame != token.lexer) {
            // TODO Maybe refactor...
            readTokenFromNewLexer(token.lexer)
        }

        if(!isTokenMatching(token.tokenId)) {
            return ParserResult.failure()
        }

        isTokenAccepted = true
        return ParserResult.success(stream.frame, stream.frame.index, stream.frame.length)
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