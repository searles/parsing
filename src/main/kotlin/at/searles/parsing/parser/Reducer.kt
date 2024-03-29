package at.searles.parsing.parser

import at.searles.parsing.lexer.TokenStream
import at.searles.parsing.parser.combinators.*
import at.searles.parsing.printer.PartialPrintTree

fun interface Reducer<A, B> {
    fun parse(stream: TokenStream, input: A): ParserResult<B>
    fun print(value: B): PartialPrintTree<A> = error("Not invertible")

    operator fun <C> plus(reducer: Reducer<B, C>): Reducer<A, C> {
        return ReducerPlusReducer(this, reducer)
    }

    operator fun <C> plus(conversion: Conversion<B, C>): Reducer<A, C> {
        return ReducerPlusConversion(this, conversion)
    }

    operator fun plus(recognizer: Recognizer): Reducer<A, B> {
        return this + recognizer.toReducer()
    }

    infix fun or(other: Reducer<A, B>): Reducer<A, B> {
        return ReducerUnion(listOf(this, other))
    }

    companion object {
        fun <A> Reducer<A, A>.rep(minCount: Int = 0): Reducer<A, A> {
            return RepeatReducer(this, minCount)
        }

        fun <A> Reducer<A, A>.join(separator: Recognizer): Reducer<A, A> {
            val parser = this + (separator + this).rep()
            val printer = (this + separator).rep() + this
            return ReducerPrinterSeparate(parser, printer)
        }

        fun <A> Reducer<A, A>.opt(): Reducer<A, A> {
            return OptionalReducer(this)
        }
    }
}