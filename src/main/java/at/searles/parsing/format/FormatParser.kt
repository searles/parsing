package at.searles.parsing.combinators

import at.searles.parsing.Parser
import at.searles.parsing.ParserStream
import at.searles.parsing.printing.ConcreteSyntaxTree

class FormatParser<T>(private val marker: Any, private val parser: Parser<T>) : Parser<T> {
    override fun parse(stream: ParserStream): T? {
        stream.formatStart(marker)
        return parser.parse(stream).also {
            if(it != null) {
                stream.formatSuccess(marker)
            } else {
                stream.formatFail(marker)
            }
        }
    }

    override fun recognize(stream: ParserStream): Boolean {
        stream.formatStart(marker)
        return parser.recognize(stream).also {
            if(it) {
                stream.formatSuccess(marker)
            } else {
                stream.formatFail(marker)
            }
        }
    }

    override fun print(item: T): ConcreteSyntaxTree? {
        return parser.print(item)?.format(marker)
    }
}
