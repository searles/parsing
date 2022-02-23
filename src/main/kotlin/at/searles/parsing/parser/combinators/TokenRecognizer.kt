package at.searles.parsing.parser.combinators

import at.searles.parsing.lexer.Lexer
import at.searles.parsing.lexer.regexp.Text
import at.searles.parsing.lexer.TokenStream
import at.searles.parsing.parser.Recognizer
import at.searles.parsing.parser.RecognizerResult
import at.searles.parsing.printer.PrintTree
import at.searles.parsing.printer.StringPrintTree

class TokenRecognizer(private val tokenId: Int, private val lexer: Lexer, private val output: String): Recognizer {
    private val printTree by lazy {
        StringPrintTree(output)
    }

    override fun parse(stream: TokenStream): RecognizerResult {
        val tokenIds = stream.getTokenIds(lexer) ?: return RecognizerResult.failure

        if(!tokenIds.contains(tokenId)) {
            return RecognizerResult.failure
        }

        val startIndex = stream.startIndex
        val endIndex = stream.endIndex

        stream.next()

        return RecognizerResult.of(startIndex, endIndex)
    }

    override fun print(): PrintTree {
        return printTree
    }

    override fun toString(): String {
        return "\'$output\'"
    }

    companion object {
        fun text(text: String, lexer: Lexer): Recognizer {
            return TokenRecognizer(lexer.createToken(Text(text)), lexer, text)
        }
    }
}