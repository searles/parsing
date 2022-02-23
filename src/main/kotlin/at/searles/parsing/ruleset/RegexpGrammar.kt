package at.searles.parsing.ruleset

import at.searles.parsing.lexer.Lexer
import at.searles.parsing.lexer.regexp.CharSet
import at.searles.parsing.lexer.regexp.Regexp
import at.searles.parsing.lexer.regexp.Text
import at.searles.parsing.parser.*
import at.searles.parsing.parser.Reducer.Companion.opt
import at.searles.parsing.parser.Reducer.Companion.rep
import at.searles.parsing.parser.combinators.*
import at.searles.parsing.parser.tools.CastBuilders.cast
import at.searles.parsing.parser.tools.CastBuilders.plus

object RegexpGrammar: Grammar {

    // TODO: Quoted strings

    override val lexer = Lexer()

    val createUnion = Fold<Regexp, Regexp, Regexp> { left, right -> left or right }
    val createDiff = Fold<Regexp, Regexp, Regexp> { left, right -> left - right }
    val createConcat = Fold<Regexp, Regexp, Regexp> { left, right -> left + right }
    val createRep = Conversion<Regexp, Regexp> { value -> value.rep() }
    val createRep1 = Conversion<Regexp, Regexp> { value -> value.rep1() }
    val createOpt = Conversion<Regexp, Regexp> { value -> value.opt() }
    val createShortest = Conversion<Regexp, Regexp> { value -> value.shortest() }

    val regexp: Parser<Regexp> by ref { union + eof }
    val union: Parser<Regexp> by ref { diff + (text("|") + diff + createUnion).rep() }

    private val diff by lazy { concat + (ch('-') + concat + createDiff).rep() }
    private val concat by lazy { repeat + (repeat + createConcat).rep() }
    private val repeat by lazy { term + (
            ch('*') + createRep or
            ch('+') + createRep1 or
            ch('?') + createOpt or
            ch('!') + createShortest).opt() }

    private val term by lazy {
            ch('.').init<Regexp>(CharSet.all()) or
            CharSetParser.charSet + cast() or
            char or
            ch('(') + union + ch(')')
    }

    object CreateChar: Conversion<Int, Regexp> {
        override fun convert(value: Int): Text {
            return Text(Character.toString(value))
        }
    }

    private val char by lazy { (escapedChar or regularChar) + CreateChar }

    private val escapedChar = rex(EscapedChars.specialChar) + EscapedChars.CreateSpecialChar
    private val regularChar = rex(EscapedChars.regularChar - CharSet('(', '\\', ')', '|', '*', '+', '-', '.')) + EscapedChars.CreateRegularChar

    fun regexp(regexpString: String): Regexp {
        return regexp.parse(regexpString).let {
            require(it.isSuccess)
            it.value
        }
    }
}