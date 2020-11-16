package at.searles.parsingtools.formatter

import at.searles.lexer.TokenStream
import at.searles.parsing.ParserStream

interface FormatListener: ParserStream.Listener, TokenStream.Listener {
}