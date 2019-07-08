package at.searles.demo

import at.searles.lexer.LexerWithHidden
import at.searles.parsing.*
import at.searles.parsing.printing.StringTree
import at.searles.parsing.utils.ast.AstNode
import at.searles.regex.RegexParser
import java.util.function.BiFunction

/**
 * Demo of inversion of a parser for the following grammar:
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
    val numMapping = object: Mapping<CharSequence, AstNode> {
        override fun parse(env: Environment, left: CharSequence, stream: ParserStream): AstNode =
                NumNode(stream, Integer.parseInt(left.toString()))

        override fun left(env: Environment, result: AstNode): CharSequence? =
                if (result is NumNode) result.value.toString() else null
    }

    val num = Parser.fromToken(numToken, numMapping, false).ref("num")

    // term: num | '(' sum ')'
    val sum = Ref<AstNode>("sum")

    val openPar = Recognizer.fromString("(", lexer, true)
    val closePar = Recognizer.fromString(")", lexer, true)

    val term = num.or(
            openPar.then(sum).then(closePar)
    ).ref("term")

    // literal: '-'? term
    // it is actually much easier to rewrite this rule
    // literal: '-' term | term


    val minus = Recognizer.fromString("-", lexer, true)

    val negate = object: Mapping<AstNode, AstNode> {
        override fun parse(env: Environment, left: AstNode, stream: ParserStream): AstNode =
            UnNode(stream, Op.Neg, left)

        override fun left(env: Environment, result: AstNode): AstNode? =
            if(result is UnNode && result.op == Op.Neg) result.arg else null
    }

    val literal =
            minus.then(term).then(negate)
            .or(term).ref("literal")

    // product: term ('*' term | '/' term)* ;

    val times = Recognizer.fromString("*", lexer, true)

    val multiply = object: Fold<AstNode, AstNode, AstNode> {
        override fun apply(env: Environment, left: AstNode, right: AstNode, stream: ParserStream): AstNode =
                BinNode(stream, Op.Mul, left, right)

        override fun leftInverse(env: Environment, result: AstNode): AstNode? =
                if(result is BinNode && result.op == Op.Mul) result.arg0 else null

        override fun rightInverse(env: Environment, result: AstNode): AstNode? =
                if(result is BinNode && result.op == Op.Mul) result.arg1 else null
    }

    val slash = Recognizer.fromString("/", lexer, true)

    val divide = object: Fold<AstNode, AstNode, AstNode> {
        override fun apply(env: Environment, left: AstNode, right: AstNode, stream: ParserStream): AstNode =
                BinNode(stream, Op.Div, left, right)

        override fun leftInverse(env: Environment, result: AstNode): AstNode? =
                if(result is BinNode && result.op == Op.Div) result.arg0 else null

        override fun rightInverse(env: Environment, result: AstNode): AstNode? =
                if(result is BinNode && result.op == Op.Div) result.arg1 else null
    }

    val product = literal.then(
            Reducer.rep(
                times.annotate(Annotation.Infix).then(literal).fold(multiply)
                .or(slash.annotate(Annotation.Infix).then(literal).fold(divide))
            )
    ).ref("product")

    // sum: product ('+' product | '-' product)* ;

    val plus = Recognizer.fromString("+", lexer, true)

    val add = object: Fold<AstNode, AstNode, AstNode> {
        override fun apply(env: Environment, left: AstNode, right: AstNode, stream: ParserStream): AstNode =
            BinNode(stream, Op.Add, left, right)

        override fun leftInverse(env: Environment, result: AstNode): AstNode? =
            if(result is BinNode && result.op == Op.Add) result.arg0 else null

        override fun rightInverse(env: Environment, result: AstNode): AstNode? =
            if(result is BinNode && result.op == Op.Add) result.arg1 else null
    }

    val sub = object: Fold<AstNode, AstNode, AstNode> {
        override fun apply(env: Environment, left: AstNode, right: AstNode, stream: ParserStream): AstNode =
                BinNode(stream, Op.Sub, left, right)

        override fun leftInverse(env: Environment, result: AstNode): AstNode? =
                if(result is BinNode && result.op == Op.Sub) result.arg0 else null

        override fun rightInverse(env: Environment, result: AstNode): AstNode? =
                if(result is BinNode && result.op == Op.Sub) result.arg1 else null
    }

    sum.set(
        product.then(
            Reducer.rep(
                plus.annotate(Annotation.Infix).then(product).fold(add)
                .or(minus.annotate(Annotation.Infix).then(product).fold(sub))
            )
        )
    )

    val env = Environment { stream, failedParser ->
        throw ParserException(
            "Error at ${stream.offset()}, expected ${failedParser.right()}"
        )
    }

    val stream = ParserStream.fromString(readLine())

    val ast = sum.parse(env, stream)

    // now pretty-print the tree

    val outTree = sum.print(env, ast)!!

    val formatting = BiFunction<Any, StringTree, StringTree> {
        category, tree -> if(category == Annotation.Infix) tree.consLeft(" ").consRight(" ") else tree
    }

    val formattedSource = outTree.toStringBuilder(StringBuilder(), formatting)

    println("Pretty-Print: $formattedSource")
}

enum class Op {Add, Sub, Mul, Div, Neg}

enum class Annotation { Infix }

class NumNode(stream: ParserStream, val value: Int): AstNode(stream)

class UnNode(stream: ParserStream, val op: Op, val arg: AstNode): AstNode(stream)
class BinNode(stream: ParserStream, val op: Op, val arg0: AstNode, val arg1: AstNode): AstNode(stream)

