package at.searles.parsing

import at.searles.lexer.Tokenizer
import at.searles.parsing.Mapping.Companion.identity
import at.searles.parsing.annotation.AnnotationRecognizer
import at.searles.parsing.combinators.*
import at.searles.parsing.printing.ConcreteSyntaxTree
import at.searles.parsing.tokens.TokenRecognizer
import at.searles.regex.CharSet
import at.searles.regex.Regex

interface Recognizer : Recognizable {
    fun print(): ConcreteSyntaxTree

    fun then(right: Recognizer): Recognizer {
        return RecognizerThenRecognizer(this, right)
    }

    fun <T> then(parser: Parser<T>): Parser<T> {
        // corresponds to prefix.
        return RecognizerThenParser(this, parser)
    }

    // corresponds to prefix.
    fun <T, U> then(right: Reducer<T, U>): Reducer<T, U> {
        return RecognizerThenReducer(this, right)
    }

    fun or(other: Recognizer): Recognizer {
        return RecognizerOrRecognizer(this, other)
    }

    fun <T> or(other: Reducer<T, T>): Reducer<T, T> {
        return this.then(identity<T>()).or(other)
    }

    fun rep(): Recognizer { //Caution in printer!
        return RecognizerRep(this)
    }

    fun plus(): Recognizer { //Caution in printer!
        return this.then(rep())
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

    fun <T> joinPlus(reducer: Reducer<T, T>): Reducer<T, T> {
        return ReducerJoinPlus(this, reducer)
    }

    fun <A> annotate(category: A): Recognizer {
        return AnnotationRecognizer(category, this)
    }

    fun ref(label: String): Recognizer {
        return RecognizerRef(this, label)
    }

    companion object {
        fun fromString(string: String, tokenizer: Tokenizer, exclusive: Boolean): Recognizer {
            val tokenId = tokenizer.add(Regex.text(string))
            return fromToken(tokenId, tokenizer, exclusive, string)
        }

        fun fromToken(tokenId: Int, tokenizer: Tokenizer, exclusive: Boolean, string: String): Recognizer {
            return TokenRecognizer(tokenId, tokenizer, exclusive, string)
        }

        /**
         * Creates a recognizer that will detect EOF (which is -1 returned from
         * the stream).
         */
        fun eof(tokenizer: Tokenizer): Recognizer {
            val eofId = tokenizer.add(CharSet.chars(-1))
            return fromToken(eofId, tokenizer, false, "")
        }
    }
}