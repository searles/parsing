package at.searles.parsing.parser.combinators

import at.searles.parsing.lexer.Token
import at.searles.parsing.parser.Parser
import at.searles.parsing.parser.ParserStream
import at.searles.parsing.parser.ParserResult
import at.searles.parsing.printer.PrintTree
import at.searles.parsing.printer.StringPrintTree

class TokenParser(private val token: Token): Parser<CharSequence> {
    override fun parse(stream: ParserStream): ParserResult<CharSequence> {
        return stream.acceptToken(token)
    }

    override fun print(value: CharSequence): PrintTree {
        return StringPrintTree(value.toString())
    }

    override fun toString(): String {
        return "<${token.tokenId}>"
    }
}