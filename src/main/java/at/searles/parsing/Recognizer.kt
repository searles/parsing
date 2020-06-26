package at.searles.parsing

import at.searles.lexer.Tokenizer
import at.searles.lexer.utils.IntSet
import at.searles.parsing.Mapping.Companion.identity
import at.searles.parsing.annotation.AnnotationRecognizer
import at.searles.parsing.combinators.*
import at.searles.parsing.printing.ConcreteSyntaxTree
import at.searles.parsing.tokens.TokenRecognizer
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
        return this + identity<T>() or other
    }

    fun rep(): Recognizer {
        return RecognizerRep(this)
    }

    fun plus(): Recognizer {
        return this + rep()
    }

    fun opt(alwaysPrint: Boolean = false): Recognizer {
        return RecognizerOpt(this)
    }

    fun optAlwaysPrint(): Recognizer {
        return RecognizerOptAlwaysPrint(this)
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

    fun <A> annotate(category: A): Recognizer {
        return AnnotationRecognizer(category, this)
    }

    fun ref(label: String): Recognizer {
        return RecognizerRef(this, label)
    }

    companion object {
        fun fromString(string: String, tokenizer: Tokenizer, exclusive: IntSet = IntSet()): Recognizer {
            val tokenId = tokenizer.add(Text(string))
            return fromToken(tokenId, string, tokenizer, exclusive)
        }

        fun fromToken(tokenId: Int, tokenString: String, tokenizer: Tokenizer, exclusive: IntSet = IntSet()): Recognizer {
            return TokenRecognizer(tokenId, tokenizer, exclusive, tokenString)
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