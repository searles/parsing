package at.searles.parsing.tokens

import at.searles.lexer.Tokenizer
import at.searles.parsing.ParserStream
import at.searles.parsing.Recognizer
import at.searles.parsing.printing.ConcreteSyntaxTree

class TokenRecognizer(val tokenId: Int, private val tokenizer: Tokenizer, private val printed: String) : Recognizer {
    override fun recognize(stream: ParserStream): Boolean {
        return stream.parseToken(tokenizer, tokenId) != null
    }

    override fun print(): ConcreteSyntaxTree {
        return ConcreteSyntaxTree.fromCharSequence(printed)
    }

    override fun toString(): String {
        return "\"" + printed + "\""
    }

}