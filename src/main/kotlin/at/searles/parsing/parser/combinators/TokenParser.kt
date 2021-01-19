package at.searles.parsing.parser.combinators

import at.searles.parsing.lexer.Token
import at.searles.parsing.parser.Parser
import at.searles.parsing.parser.ParserStream
import at.searles.parsing.parser.ParserResult
import at.searles.parsing.printer.PrintResult

class TokenParser(private val token: Token): Parser<String> {
    override fun parse(stream: ParserStream): ParserResult<String> {
        return stream.acceptToken(token)
    }

    override fun print(value: String): PrintResult {
        return PrintResult.success(value)
    }
}