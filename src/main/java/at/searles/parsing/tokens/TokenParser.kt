package at.searles.parsing.tokens

import at.searles.lexer.Tokenizer
import at.searles.parsing.Parser
import at.searles.parsing.ParserStream
import at.searles.parsing.printing.ConcreteSyntaxTree

class TokenParser(val tokenId: Int, val tokenizer: Tokenizer) : Parser<CharSequence> {
    override fun recognize(stream: ParserStream): Boolean {
        return stream.parseToken(tokenizer, tokenId) != null
    }

    override fun parse(stream: ParserStream): CharSequence? {
        return stream.parseToken(tokenizer, tokenId)
    }

    override fun print(item: CharSequence): ConcreteSyntaxTree? {
        return ConcreteSyntaxTree.fromCharSequence(item)
    }

    override fun toString(): String {
        return "<$tokenId>"
    }

}