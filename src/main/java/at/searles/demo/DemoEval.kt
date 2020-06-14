package at.searles.demo

import at.searles.lexer.Lexer
import at.searles.lexer.SkipTokenizer
import at.searles.parsing.*
import at.searles.parsing.ParserStream.Companion.createParserStream
import at.searles.parsing.Reducer.Companion.rep
import at.searles.regexparser.StringToRegex

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
    val lexer = SkipTokenizer(Lexer())

    // ignore white spaces
    val wsTokenId = lexer.add(StringToRegex.parse("[\n\r\t ]+"))
    lexer.addSkipped(wsTokenId)

    // num: [0-9]* ;
    val numTokenId = lexer.add(StringToRegex.parse("[0-9]+"))
    val numMapping = Mapping.create<CharSequence, Int> { it.toString().toInt()}

    // ref here provides a label that is used by the parser's toString-method.
    // This improves debugging because in case of an error it is easier to spot 
    // the concrete parser.
    val num = Parser.fromToken(numTokenId, lexer, true, numMapping).ref("num")
    
    // term: num | '(' sum ')'
    val sum = Ref<Int>("sum")

    val openPar = Recognizer.fromString("(", lexer, false)
    val closePar = Recognizer.fromString(")", lexer, false)

    val term = (
            num or
            openPar + sum + closePar
    ).ref("term")

    // literal: '-'? term
    // it is actually much easier to rewrite this rule
    // literal: '-' term | term

    val minus = Recognizer.fromString("-", lexer, false)

    val negate = Mapping.create<Int, Int> { -it }

    val literal = (
            minus + term + negate or
            term
    ).ref("literal")

    // product: term ('*' term | '/' term)* ;

    val times = Recognizer.fromString("*", lexer, false)

    val multiply = Fold.create<Int, Int, Int> { left, right -> left * right }

    val slash = Recognizer.fromString("/", lexer, false)

    val divide = Fold.create<Int, Int, Int> { left, right -> left / right }

    val product = (
            literal + (
                    times + literal.fold(multiply) or
                    slash + literal.fold(divide)
            ).rep()
    ).ref("product")

    // sum: product ('+' product | '-' product)* ;

    val plus = Recognizer.fromString("+", lexer, false)

    val add = Fold.create<Int, Int, Int> { left, right -> left + right }

    val sub = Fold.create<Int, Int, Int> { left, right -> left - right }

    sum.set(
            product + (
                    plus + product.fold(add) or
                    minus + product.fold(sub)
            ).rep()
    )

    val stream = readLine()!!.createParserStream()
    // To use a reader, the following can be used:
    // val stream = ParserStream(TokStream.fromCharStream(ReaderCharStream(InputStreamReader(System.`in`))))

    println("Result = ${sum.parse(stream)}")
    println("Position in stream: ${stream.end}")
}
