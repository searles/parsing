package at.searles.demo

import at.searles.lexer.LexerWithHidden
import at.searles.parsing.*
import at.searles.regex.RegexParser
import java.lang.RuntimeException

/**
 * Demo of a simple evaluator of mathematical expressions for
 * the following grammar:
 *
 * num: [0-9]* ;
 * term: num | '(' sum ')' ;
 * literal: '-'? term ;
 * product: term ('*' term | '/' term)* ;
 * sum: product ('+' product | '-' product)* ;
 */
fun main() {
    // Create a lexer
    val lexer = LexerWithHidden()

    // ignore white spaces
    lexer.hiddenToken(RegexParser.parse("[\n\r\t ]+"))

    // num: [0-9]* ;
    val numToken = lexer.token(RegexParser.parse("[0-9]+"))
    val numMapping = Mapping<CharSequence, Int> {
        _, left, _ -> Integer.parseInt(left.toString())
    }
    val num = Parser.fromToken(numToken, numMapping, false).ref("num")

    // term: num | '(' sum ')'
    val sum = Ref<Int>("sum")

    val openPar = Recognizer.fromString("(", lexer, true)
    val closePar = Recognizer.fromString(")", lexer, true)

    val term = num.or(
            openPar.then(sum).then(closePar)
    ).ref("term")

    // literal: '-'? term
    // it is actually much easier to rewrite this rule
    // literal: '-' term | term

    val minus = Recognizer.fromString("-", lexer, true)

    val negate = Mapping<Int, Int> { _, value, _ -> -value }

    val literal =
            minus.then(term).then(negate)
            .or(term).ref("literal")

    // product: term ('*' term | '/' term)* ;

    val times = Recognizer.fromString("*", lexer, true)

    val multiply = Fold<Int, Int, Int> { _, left, right, _ ->
        left * right
    }

    val slash = Recognizer.fromString("/", lexer, true)

    val divide = Fold<Int, Int, Int> { _, left, right, _ ->
        left / right
    }

    val product = literal.then(
            Reducer.rep(
                times.then(literal).fold(multiply)
                .or(slash.then(literal).fold(divide))
            )
    ).ref("product")

    // sum: product ('+' product | '-' product)* ;

    val plus = Recognizer.fromString("+", lexer, true)

    val add = Fold<Int, Int, Int> { _, left, right, _ ->
        left + right
    }

    val sub = Fold<Int, Int, Int> { _, left, right, _ ->
        left - right
    }

    sum.set(
        product.then(
            Reducer.rep(
                plus.then(product).fold(add)
                .or(minus.then(product).fold(sub))
            )
        )
    )

    val env = Environment { stream, failedParser ->
        throw ParserException(
            "Error at ${stream.offset()}, expected ${failedParser.right()}"
        )
    }

    val stream = ParserStream.fromString(readLine())

    println("Result = ${sum.parse(env, stream)}")
    println("Position in stream: ${stream.end()}")
}

class ParserException(msg: String) : RuntimeException(msg)
