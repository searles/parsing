package at.searles.demo

import at.searles.lexer.LexerWithHidden
import at.searles.parsing.*
import at.searles.regex.RegexParser

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
    lexer.addHiddenToken(RegexParser.parse("[\n\r\t ]+"))

    // num: [0-9]* ;
    val numToken = lexer.token(RegexParser.parse("[0-9]+"))
    val numMapping = Mapping<CharSequence, Int> { _, left -> Integer.parseInt(left.toString()) }
    
    // ref here provides a label that is used by the parser's toString-method.
    // This improves debugging because in case of an error it is easier to spot 
    // the concrete parser.
    val num = Parser.fromToken(numToken, numMapping, false).ref("num") 
    
    // term: num | '(' sum ')'
    val sum = Ref<Int>("sum")

    val openPar = Recognizer.fromString("(", lexer, false)
    val closePar = Recognizer.fromString(")", lexer, false)

    val term = num.or(
            openPar.then(sum).then(closePar)
    ).ref("term")

    // literal: '-'? term
    // it is actually much easier to rewrite this rule
    // literal: '-' term | term

    val minus = Recognizer.fromString("-", lexer, false)

    val negate = Mapping<Int, Int> { _, value -> -value }

    val literal =
            minus.then(term).then(negate)
                    .or(term).ref("literal")

    // product: term ('*' term | '/' term)* ;

    val times = Recognizer.fromString("*", lexer, false)

    val multiply = Fold<Int, Int, Int> { _, left, right -> left * right }

    val slash = Recognizer.fromString("/", lexer, false)

    val divide = Fold<Int, Int, Int> { _, left, right -> left / right }

    val product = literal.then(
            Reducer.rep(
                    times.then(literal).fold(multiply)
                            .or(slash.then(literal).fold(divide))
            )
    ).ref("product")

    // sum: product ('+' product | '-' product)* ;

    val plus = Recognizer.fromString("+", lexer, false)

    val add = Fold<Int, Int, Int> { _, left, right -> left + right }

    val sub = Fold<Int, Int, Int> { _, left, right -> left - right }

    sum.set(
            product.then(
                    Reducer.rep(
                            plus.then(product).fold(add)
                                    .or(minus.then(product).fold(sub))
                    )
            )
    )

    val stream = ParserStream.fromString(readLine())
    // To use a reader, the following can be used:
    // val stream = ParserStream(TokStream.fromCharStream(ReaderCharStream(InputStreamReader(System.`in`))))

    println("Result = ${sum.parse(stream)}")
    println("Position in stream: ${stream.end()}")
}
