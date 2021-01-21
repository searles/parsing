package at.searles.parsing.ruleset

import at.searles.parsing.lexer.Lexer
import at.searles.parsing.lexer.fsa.Interval
import at.searles.parsing.lexer.regexp.CharSet
import at.searles.parsing.parser.*
import at.searles.parsing.parser.Reducer.Companion.rep
import at.searles.parsing.printer.PrintTree

object CharSetParser: ParserRule<CharSet> {
    override val lexer: Lexer = Lexer()

    private val charSet by lazy { text("[") + invertableSet + text("]") }

    private object InvertSet: Conversion<CharSet, CharSet> {
        override fun convert(value: CharSet): CharSet {
            return value.invert()
        }
    }

    private val invertableSet: Parser<CharSet> by lazy {
        text("^") + set + InvertSet or
        set
    }

    private object AppendToSet: Fold<CharSet, Interval, CharSet> {
        override fun fold(left: CharSet, right: Interval): CharSet {
            return left + CharSet(right.first .. right.last)
        }
    }

    private val set: Parser<CharSet> by lazy {
        InitValue { CharSet.empty() } + (
                charOrRange + AppendToSet
        ).rep(1)
    }

    private object CreateRange: Fold<Int, Int, Interval> {
        override fun fold(left: Int, right: Int): Interval {
            return Interval(left .. right)
        }
    }

    private object CreateSingleChar: Conversion<Int, Interval> {
        override fun convert(value: Int): Interval {
            return Interval(value)
        }
    }

    private val startChar =
        rex(EscapedChars.specialChar) + EscapedChars.CreateSpecialChar or
        rex(EscapedChars.regularChar - CharSet(']')) + EscapedChars.CreateRegularChar

    private val endChar =
        rex(EscapedChars.specialChar) + EscapedChars.CreateSpecialChar or
                rex(EscapedChars.regularChar) + EscapedChars.CreateRegularChar

    private val charOrRange: Parser<Interval> by lazy {
        startChar + (
            text("-") + endChar + CreateRange or
            CreateSingleChar
        )
    }

    override fun parse(stream: ParserStream): ParserResult<CharSet> {
        return charSet.parse(stream)
    }

    override fun print(value: CharSet): PrintTree {
        return charSet.print(value)
    }

}
