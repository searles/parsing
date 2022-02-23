package at.searles.parsing.parser

import at.searles.parsing.lexer.TokenStream
import at.searles.parsing.parser.combinators.InitializerPlusReducer
import at.searles.parsing.printer.PrintTree

fun interface Initializer<A> {
    fun initialize(): A
    fun consume(value: A): Boolean = error("Not invertible function")

    operator fun <B> plus(reducer: Reducer<A, B>): Parser<B> {
        return InitializerPlusReducer(this, reducer)
    }

    operator fun <B> plus(conversion: Conversion<A, B>): Initializer<B> {
        return object: Initializer<B> {
            override fun initialize(): B {
                return conversion.convert(this@Initializer.initialize())
            }

            override fun consume(value: B): Boolean {
                val inverse = conversion.invert(value)
                if(!inverse.isSuccess) return false
                return consume(inverse.value)
            }
        }
    }

    operator fun <A0, B> plus(fold: Fold<A0, A, B>): Conversion<A0, B> {
        return object: Conversion<A0, B> {
            override fun convert(value: A0): B {
                return fold.fold(value, initialize())
            }

            override fun invert(value: B): FnResult<A0> {
                val rightInverse = fold.invertRight(value)

                if(!rightInverse.isSuccess || !consume(rightInverse.value)) {
                    return FnResult.failure
                }

                return fold.invertLeft(value)
            }
        }
    }

    fun asParser(): Parser<A> {
        return object: Parser<A> {
            override fun parse(stream: TokenStream): ParserResult<A> {
                return ParserResult.of(initialize(), stream.startIndex, 0)
            }

            override fun print(value: A): PrintTree {
                return if(consume(value)) {
                    PrintTree.empty
                } else {
                    PrintTree.failure
                }
            }
        }
    }
}