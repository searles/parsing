package at.searles.parsing.parser.combinators

import at.searles.parsing.lexer.Frame
import at.searles.parsing.lexer.Token
import at.searles.parsing.parser.Parser
import at.searles.parsing.parser.ParserResult
import at.searles.parsing.parser.ParserStream

class TokenParser(private val token: Token): Parser<Frame> {
    override fun parse(stream: ParserStream): ParserResult<Frame> {
        return stream.acceptToken(token)
    }
}