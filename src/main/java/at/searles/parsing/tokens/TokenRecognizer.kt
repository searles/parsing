package at.searles.parsing.tokens

import at.searles.lexer.Tokenizer
import at.searles.parsing.ParserStream
import at.searles.parsing.Recognizer
import at.searles.parsing.printing.ConcreteSyntaxTree

class TokenRecognizer(val tokId: Int, private val tokenizer: Tokenizer, val exclusive: Boolean, val printed: String) : Recognizer {
    override fun recognize(stream: ParserStream): Boolean {
        return stream.parseToken(tokenizer, tokId, exclusive) != null
    }

    override fun print(): ConcreteSyntaxTree {
        return ConcreteSyntaxTree.Companion.fromCharSequence(printed)
    }

    override fun toString(): String {
        return "\"" + printed + "\""
    }

}