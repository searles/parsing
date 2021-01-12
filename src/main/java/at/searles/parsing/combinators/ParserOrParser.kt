package at.searles.parsing.combinators

import at.searles.parsing.*
import at.searles.parsing.printing.ConcreteSyntaxTree

/**
 * Parser for options. The order is important. First one to
 * succeed is the one that is used.
 */
class ParserOrParser<T>(private val parseOrder: List<Parser<T>>, private val printOrder: List<Parser<T>>) : Parser<T> {

    override fun or(other: Parser<T>): Parser<T> {
        return ParserOrParser(parseOrder + other, printOrder + other)
    }

    override fun orSwapOnPrint(other: Parser<T>): Parser<T> {
        return ParserOrParser(parseOrder + other, listOf(other) + printOrder)
    }

    override fun parse(stream: ParserStream): T? {
        for(choice in parseOrder) {
            stream.parse(choice)?.let {
                return it
            }
        }

        return null
    }

    override fun recognize(stream: ParserStream): Boolean {
        for(choice in parseOrder) {
            if(stream.recognize(choice, true)) {
                return true
            }
        }

        return false
    }

    override fun print(item: T): ConcreteSyntaxTree? {
        for(choice in printOrder) {
            choice.print(item)?.let {
                return it
            }
        }

        return null
    }

    override fun <L, V> plus(fold: Fold<L, T, V>): Reducer<L, V> {
        return ReducerOrReducer(
            parseOrder.map { it + fold },
            printOrder.map { it + fold },
        )
    }

    override fun <U> plus(right: Reducer<T, U>): Parser<U> {
        return ParserOrParser(
            parseOrder.map { it + right },
            printOrder.map { it + right }
        )
    }

    override fun plus(right: Recognizer): Parser<T> {
        return ParserOrParser(
            parseOrder.map { it + right },
            printOrder.map { it + right }
        )
    }

    override fun <U> plus(right: Parser<U>): Parser<Pair<T, U>> {
        return ParserOrParser(
            parseOrder.map { it + right },
            printOrder.map { it + right }
        )
    }

    override fun toString(): String {
        return "${parseOrder.first()}.or(${parseOrder.drop(1).joinToString(", ")})"
    }
}