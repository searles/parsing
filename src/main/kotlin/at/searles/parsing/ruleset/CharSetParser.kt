package at.searles.parsing.ruleset

import at.searles.parsing.lexer.Lexer
import at.searles.parsing.lexer.regexp.CharSet
import at.searles.parsing.parser.*
import at.searles.parsing.parser.Reducer.Companion.rep
import at.searles.parsing.parser.tools.InitValue

object CharSetParser: Grammar {
    override val lexer: Lexer = Lexer()

    val charSet by lazy { ch('[') + invertableSet + ch(']') }

    private object InvertSet: Conversion<CharSet, CharSet> {
        override fun convert(value: CharSet): CharSet {
            return value.invert()
        }
    }

    private val invertableSet: Parser<CharSet> by lazy {
        text("^") + set + InvertSet or
        set
    }

    private object AppendToSet: Fold<CharSet, IntRange, CharSet> {
        override fun fold(left: CharSet, right: IntRange): CharSet {
            return left.union(CharSet(right.first .. right.last))
        }
    }

    private val set: Parser<CharSet> by lazy {
        InitValue(CharSet.empty()) + (
                charOrRange + AppendToSet
        ).rep(1)
    }

    private object CreateRange: Fold<Int, Int, IntRange> {
        override fun fold(left: Int, right: Int): IntRange {
            return (left .. right)
        }
    }

    private object CreateSingleChar: Conversion<Int, IntRange> {
        override fun convert(value: Int): IntRange {
            return (value .. value)
        }
    }

    private val startChar =
        rex(EscapedChars.specialChar) + EscapedChars.CreateSpecialChar or
        rex(EscapedChars.regularChar - CharSet(']')) + EscapedChars.CreateRegularChar

    private val endChar =
        rex(EscapedChars.specialChar) + EscapedChars.CreateSpecialChar or
                rex(EscapedChars.regularChar) + EscapedChars.CreateRegularChar

    private val charOrRange: Parser<IntRange> by lazy {
        startChar + (
            text("-") + endChar + CreateRange or
            CreateSingleChar
        )
    }
}
