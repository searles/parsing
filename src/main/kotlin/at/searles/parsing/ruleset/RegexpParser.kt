package at.searles.parsing.ruleset

import at.searles.parsing.lexer.Lexer
import at.searles.parsing.lexer.regexp.CharSet
import at.searles.parsing.lexer.regexp.Regexp
import at.searles.parsing.lexer.regexp.Text
import at.searles.parsing.parser.*
import at.searles.parsing.parser.Reducer.Companion.opt
import at.searles.parsing.parser.Reducer.Companion.rep
import at.searles.parsing.parser.combinators.*
import at.searles.parsing.printer.PrintTree

object RegexpParser: ParserRule<Regexp> {

    override val lexer = Lexer()

    val createUnion get() = object: Fold<Regexp, Regexp, Regexp> {
        override fun fold(left: Regexp, right: Regexp): Regexp {
            return left or right
        }
    }

    val createDiff get() = object: Fold<Regexp, Regexp, Regexp> {
        override fun fold(left: Regexp, right: Regexp): Regexp {
            return left - right
        }
    }

    val createConcat get() = object: Fold<Regexp, Regexp, Regexp> {
        override fun fold(left: Regexp, right: Regexp): Regexp {
            return left + right
        }
    }

    val createRep get() = object: Conversion<Regexp, Regexp> {
        override fun convert(value: Regexp): Regexp {
            return value.rep()
        }
    }

    val createRep1 get() = object: Conversion<Regexp, Regexp> {
        override fun convert(value: Regexp): Regexp {
            return value.rep1()
        }
    }

    val union: Parser<Regexp> by ref { diff + (text("|") + diff + createUnion).rep() }

    val diff by lazy { concat + (text("-") + concat + createDiff).rep() }
    val concat by lazy { repeat + (repeat + createConcat).rep() }
    val repeat by lazy { term + (text("*") + createRep or text("+") + createRep1).opt() }

    val term by lazy {
            CharSetParser + cast<CharSet, Regexp>() or
            char or
            text(".").init(CharSet.all()) or
            text("(") + union + text(")")
    }

    val createChar = object: Conversion<Int, Regexp> {
        override fun convert(value: Int): Text {
            return Text(Character.toString(value))
        }
    }

    val char by lazy { (escapedChar or regularChar) + createChar }

    val escapedChar = rex(EscapedChars.specialChar) + EscapedChars.createSpecialChar
    val regularChar = rex(EscapedChars.regularChar - CharSet('(', '\\', ')', '|', '*', '+', '-', '.')) + EscapedChars.createRegularChar

    override fun parse(stream: ParserStream): ParserResult<Regexp> {
        return union.parse(stream)
    }

    override fun print(value: Regexp): PrintTree {
        TODO("Not yet implemented")
    }
}