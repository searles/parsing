package at.searles.parsing.parser.combinators

import at.searles.parsing.lexer.Lexer
import at.searles.parsing.lexer.Token
import at.searles.parsing.lexer.regexp.Text
import at.searles.parsing.parser.ParserStream
import at.searles.parsing.parser.Recognizer
import at.searles.parsing.parser.RecognizerResult
import at.searles.parsing.printer.PrintTree
import at.searles.parsing.printer.StringPrintTree

class TokenRecognizer(private val token: Token, private val output: String): Recognizer {
    private val printTree by lazy {
        StringPrintTree(output)
    }

    override fun parse(stream: ParserStream): RecognizerResult {
        val tokenResult = stream.acceptToken(token)

        return if(tokenResult.isSuccess) {
            RecognizerResult.of(tokenResult.index, tokenResult.length)
        } else {
            RecognizerResult.failure
        }
    }

    override fun print(): PrintTree {
        return printTree
    }

    companion object {
        fun text(text: String, lexer: Lexer): Recognizer {
            return TokenRecognizer(lexer.createToken(Text(text)), text)
        }
    }
}