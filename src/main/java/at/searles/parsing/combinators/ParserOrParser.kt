package at.searles.parsing.combinators

import at.searles.parsing.*
import at.searles.parsing.printing.ConcreteSyntaxTree

/**
 * Parser for options. The order is important. First one to
 * succeed is the one that is used.
 */
class ParserOrParser<T>(private vararg val choices: Parser<T>) : Parser<T> {
    override fun or(other: Parser<T>): Parser<T> {
        return ParserOrParser(*choices, other)
    }

    override fun parse(stream: ParserStream): T? {
        for(choice in choices) {
            stream.parse(choice)?.let {
                return it
            }
        }

        return null
    }

    override fun recognize(stream: ParserStream): Boolean {
        for(choice in choices) {
            if(stream.recognize(choice, true)) {
                return true
            }
        }

        return false
    }

    override fun print(item: T): ConcreteSyntaxTree? {
        for(choice in choices) {
            choice.print(item)?.let {
                return it
            }
        }

        return null
    }

    override fun <L, V> plus(fold: Fold<L, T, V>): Reducer<L, V> {
        return ReducerOrReducer(*choices.map { it + fold }.toTypedArray())
    }

    override fun <U> plus(right: Reducer<T, U>): Parser<U> {
        return ParserOrParser(*choices.map { it + right }.toTypedArray())
    }

    override fun plus(right: Recognizer): Parser<T> {
        return ParserOrParser(*choices.map { it + right }.toTypedArray())
    }

    override fun <U> plus(right: Parser<U>): Parser<Pair<T, U>> {
        return ParserOrParser(*choices.map { it + right }.toTypedArray())
    }

    override fun toString(): String {
        return "${choices.first()}.or(${choices.drop(1).joinToString(", ")})"
    }

}