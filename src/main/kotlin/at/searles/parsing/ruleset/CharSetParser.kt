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

    private val invertSet = object: Conversion<CharSet, CharSet> {
        override fun convert(value: CharSet): CharSet {
            return value.invert()
        }
    }

    private val invertableSet: Parser<CharSet> by lazy {
        text("^") + set + invertSet or
        set
    }

    private val appendToSet = object: Fold<CharSet, Interval, CharSet> {
        override fun fold(left: CharSet, right: Interval): CharSet {
            return left + CharSet(right.first .. right.last)
        }
    }

    private val set: Parser<CharSet> by lazy {
        create { CharSet.empty() } + (
                charOrRange + appendToSet
        ).rep(1)
    }

    private val createRange = object: Fold<Int, Int, Interval> {
        override fun fold(left: Int, right: Int): Interval {
            return Interval(left .. right)
        }
    }

    private val createSingleChar = object: Conversion<Int, Interval> {
        override fun convert(value: Int): Interval {
            return Interval(value)
        }
    }

    private val startChar =
        rex(EscapedChars.specialChar) + EscapedChars.createSpecialChar or
        rex(EscapedChars.regularChar - CharSet(']')) + EscapedChars.createRegularChar

    private val endChar =
        rex(EscapedChars.specialChar) + EscapedChars.createSpecialChar or
                rex(EscapedChars.regularChar) + EscapedChars.createRegularChar

    private val charOrRange: Parser<Interval> by lazy {
        startChar + (
            text("-") + endChar + createRange or
            createSingleChar
        )
    }

    override fun parse(stream: ParserStream): ParserResult<CharSet> {
        return charSet.parse(stream)
    }

    override fun print(value: CharSet): PrintTree {
        TODO("Not yet implemented")
    }

}
