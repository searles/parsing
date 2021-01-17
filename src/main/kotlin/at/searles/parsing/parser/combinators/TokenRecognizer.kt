package at.searles.parsing.parser.combinators

import at.searles.parsing.lexer.Token
import at.searles.parsing.parser.ParserResult
import at.searles.parsing.parser.ParserStream
import at.searles.parsing.parser.Recognizer

class TokenRecognizer(private val token: Token): Recognizer {
    override fun parse(stream: ParserStream): ParserResult<Nothing?> {
        return if(stream.acceptToken(token).isSuccess) {
            stream.createSuccess(null)
        } else {
            stream.createFail()
        }
    }
}