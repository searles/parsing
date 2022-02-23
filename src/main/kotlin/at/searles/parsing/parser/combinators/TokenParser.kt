package at.searles.parsing.parser.combinators

import at.searles.parsing.lexer.Lexer
import at.searles.parsing.lexer.TokenStream
import at.searles.parsing.parser.*
import at.searles.parsing.printer.PrintTree
import at.searles.parsing.printer.StringPrintTree

class TokenParser<A>(private val tokenId: Int, private val lexer: Lexer, private val fn: Conversion<CharSequence, A>): Parser<A> {
    constructor(tokenId: Int, lexer: Lexer, fn: (CharSequence) -> A): this(tokenId, lexer, object: Conversion<CharSequence, A> {
        override fun convert(value: CharSequence): A {
            return fn(value)
        }

        override fun invert(value: A): FnResult<CharSequence> {
            return FnResult.success(value.toString())
        }
    })

    override fun parse(stream: TokenStream): ParserResult<A> {
        val tokenIds = stream.getTokenIds(lexer) ?: return ParserResult.failure

        if(!tokenIds.contains(tokenId)) {
            return ParserResult.failure
        }

        val startIndex = stream.startIndex
        val endIndex = stream.endIndex
        val value = fn.convert(stream.frame)
        stream.next()

        return ParserResult.of(value, startIndex, endIndex)
    }

    override fun print(value: A): PrintTree {
        val inverted = fn.invert(value)

        return if(inverted.isSuccess) {
            StringPrintTree(inverted.value.toString())
        } else {
            PrintTree.failure
        }
    }

    override fun toString(): String {
        return "[${tokenId}]"
    }

    object StringCreator: Conversion<CharSequence, String> {
        override fun convert(value: CharSequence): String {
            return value.toString()
        }

        override fun invert(value: String): FnResult<CharSequence> {
            return FnResult.success(value)
        }
    }

    companion object {
        operator fun invoke(tokenId: Int, lexer: Lexer): TokenParser<String> {
            return TokenParser(tokenId, lexer, StringCreator)
        }
    }
}