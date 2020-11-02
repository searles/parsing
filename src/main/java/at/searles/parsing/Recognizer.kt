package at.searles.parsing

import at.searles.lexer.Tokenizer
import at.searles.parsing.combinators.*
import at.searles.parsing.label.RecognizerRef
import at.searles.parsing.printing.ConcreteSyntaxTree
import at.searles.parsing.tokens.TokenRecognizer
import at.searles.parsingtools.common.Init
import at.searles.regexp.CharSet
import at.searles.regexp.Text

interface Recognizer : Recognizable {
    fun print(): ConcreteSyntaxTree

    operator fun plus(right: Recognizer): Recognizer {
        return RecognizerThenRecognizer(this, right)
    }

    operator fun <T> plus(parser: Parser<T>): Parser<T> {
        // corresponds to prefix.
        return RecognizerThenParser(this, parser)
    }

    // corresponds to prefix.
    operator fun <T, U> plus(right: Reducer<T, U>): Reducer<T, U> {
        return RecognizerThenReducer(this, right)
    }

    infix fun or(other: Recognizer): Recognizer {
        return RecognizerOrRecognizer(this, other)
    }

    infix fun <T> or(other: Reducer<T, T>): Reducer<T, T> {
        return this + Mapping.create<T, T> { it } or other
    }

    fun rep(): Recognizer {
        return RecognizerRep(this)
    }

    fun plus(): Recognizer {
        return this + rep()
    }

    fun opt(): Recognizer {
        return RecognizerOpt(this)
    }

    fun flag(): Parser<Boolean> {
        return this + Init(true) or Init(false)
    }

    fun optAlwaysPrint(): Recognizer {
        return RecognizerOptAlwaysPrint(this)
    }

    fun <T> init(value: T): Parser<T> {
        return this + Init(value)
    }

    /**
     * Creates a reducer for a possibly empty csv-alike structure
     */
    fun <T> join(reducer: Reducer<T, T>): Reducer<T, T> {
        return ReducerJoin(this, reducer)
    }

    fun <T> join1(reducer: Reducer<T, T>): Reducer<T, T> {
        return ReducerJoinPlus(this, reducer)
    }

    fun ref(label: String): Recognizer {
        return RecognizerRef(label).apply {
            this.ref = this@Recognizer
        }
    }

    companion object {
        fun fromString(string: String, tokenizer: Tokenizer): Recognizer {
            val tokenId = tokenizer.add(Text(string))
            return fromToken(tokenId, string, tokenizer)
        }

        fun fromToken(tokenId: Int, tokenString: String, tokenizer: Tokenizer): Recognizer {
            return TokenRecognizer(tokenId, tokenizer, tokenString)
        }

        /**
         * Creates a recognizer that will detect EOF (which is -1 returned from
         * the stream).
         */
        fun eof(tokenizer: Tokenizer): Recognizer {
            val eofId = tokenizer.add(CharSet.chars(-1))
            return fromToken(eofId, "<eof>", tokenizer)
        }
    }
}