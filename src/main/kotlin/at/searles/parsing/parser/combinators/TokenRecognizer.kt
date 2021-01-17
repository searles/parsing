package at.searles.parsing.parser.combinators

import at.searles.parsing.lexer.Lexer
import at.searles.parsing.lexer.Token
import at.searles.parsing.lexer.regexp.Text
import at.searles.parsing.parser.ParserResult
import at.searles.parsing.parser.ParserStream
import at.searles.parsing.parser.Recognizer

class TokenRecognizer(private val token: Token, override val output: String): Recognizer {
    override fun parse(stream: ParserStream): ParserResult<Nothing?> {
        val tokenResult = stream.acceptToken(token)

        return if(tokenResult.isSuccess) {
            ParserResult.success(null, tokenResult.index, tokenResult.length)
        } else {
            ParserResult.failure()
        }
    }

    companion object {
        fun text(text: String, lexer: Lexer): Recognizer {
            return TokenRecognizer(lexer.createToken(Text(text)), text)
        }
    }
}