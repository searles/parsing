package at.searles.parsing.tokens

import at.searles.lexer.Tokenizer
import at.searles.lexer.utils.IntervalSet
import at.searles.parsing.Parser
import at.searles.parsing.ParserStream
import at.searles.parsing.printing.ConcreteSyntaxTree

class TokenParser(val tokenId: Int, val tokenizer: Tokenizer, val exclusive: IntervalSet = IntervalSet()) : Parser<CharSequence> {
    override fun recognize(stream: ParserStream): Boolean {
        return stream.parseToken(tokenizer, tokenId, exclusive) != null
    }

    override fun parse(stream: ParserStream): CharSequence? {
        return stream.parseToken(tokenizer, tokenId, exclusive)
    }

    override fun print(item: CharSequence): ConcreteSyntaxTree? {
        return ConcreteSyntaxTree.fromCharSequence(item)
    }

    override fun toString(): String {
        return "<$tokenId/$exclusive>"
    }

}