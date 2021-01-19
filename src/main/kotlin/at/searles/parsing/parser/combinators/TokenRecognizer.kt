package at.searles.parsing.parser.combinators

import at.searles.parsing.lexer.Lexer
import at.searles.parsing.lexer.Token
import at.searles.parsing.lexer.regexp.Text
import at.searles.parsing.parser.ParserStream
import at.searles.parsing.parser.Recognizer
import at.searles.parsing.parser.RecognizerResult

class TokenRecognizer(private val token: Token, override val output: String): Recognizer {
    override fun parse(stream: ParserStream): RecognizerResult {
        val tokenResult = stream.acceptToken(token)

        return if(tokenResult.isSuccess) {
            RecognizerResult.success(tokenResult.index, tokenResult.length)
        } else {
            RecognizerResult.failure()
        }
    }

    companion object {
        fun text(text: String, lexer: Lexer): Recognizer {
            return TokenRecognizer(lexer.createToken(Text(text)), text)
        }
    }
}