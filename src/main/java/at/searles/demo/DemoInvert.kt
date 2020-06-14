package at.searles.demo

import at.searles.lexer.Lexer
import at.searles.lexer.SkipTokenizer
import at.searles.parsing.*
import at.searles.parsing.ParserStream.Companion.createParserStream
import at.searles.parsing.Reducer.Companion.rep
import at.searles.parsing.printing.ConcreteSyntaxTree
import at.searles.parsing.printing.CstPrinter
import at.searles.parsing.printing.StringOutStream
import at.searles.regexparser.StringToRegex

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
    val lexer = SkipTokenizer(Lexer())
            
    // ignore white spaces
    val wsTokenId = lexer.add(StringToRegex.parse("[\n\r\t ]+"))
    lexer.addSkipped(wsTokenId)

    // num: [0-9]* ;
    val numTokenId = lexer.add(StringToRegex.parse("[0-9]+"))
    val numMapping = object : Mapping<CharSequence, AstNode> {
        override fun parse(stream: ParserStream, input: CharSequence): AstNode =
                NumNode(Integer.parseInt(input.toString()))

        override fun left(result: AstNode): CharSequence? =
                if (result is NumNode) result.value.toString() else null
    }

    val num = Parser.fromToken(numTokenId, lexer, true, numMapping).ref("num")

    // term: num | '(' sum ')'
    val sum = Ref<AstNode>("sum")

    val openPar = Recognizer.fromString("(", lexer, false)
    val closePar = Recognizer.fromString(")", lexer, false)

    val term = (num or openPar + sum + closePar).ref("term")

    // literal: '-'? term
    // it is actually much easier to rewrite this rule
    // literal: '-' term | term


    val minus = Recognizer.fromString("-", lexer, false)

    val negate = object : Mapping<AstNode, AstNode> {
        override fun parse(stream: ParserStream, input: AstNode): AstNode =
                OpNode(Op.Neg, input)

        override fun left(result: AstNode): AstNode? =
                if (result is OpNode && result.op == Op.Neg) result.args[0] else null
    }

    val literal =
            ((minus + term + negate) or term).ref("literal")

    // product: term ('*' term | '/' term)* ;

    val times = Recognizer.fromString("*", lexer, false)

    val multiply = object : Fold<AstNode, AstNode, AstNode> {
        override fun apply(stream: ParserStream, left: AstNode, right: AstNode): AstNode =
                OpNode(Op.Mul, left, right)

        override fun leftInverse(result: AstNode): AstNode? =
                if (result is OpNode && result.op == Op.Mul) result.args[0] else null

        override fun rightInverse(result: AstNode): AstNode? =
                if (result is OpNode && result.op == Op.Mul) result.args[1] else null
    }

    val slash = Recognizer.fromString("/", lexer, false)

    val divide = object : Fold<AstNode, AstNode, AstNode> {
        override fun apply(stream: ParserStream, left: AstNode, right: AstNode): AstNode =
                OpNode(Op.Div, left, right)

        override fun leftInverse(result: AstNode): AstNode? =
                if (result is OpNode && result.op == Op.Div) result.args[0] else null

        override fun rightInverse(result: AstNode): AstNode? =
                if (result is OpNode && result.op == Op.Div) result.args[1] else null
    }

    val product = (literal + ((times.annotate(FormatOp.Infix) + literal).fold(multiply) or
            (slash.annotate(FormatOp.Infix) + literal).fold(divide)).rep()).ref("product")

    // sum: product ('+' product | '-' product)* ;

    val plus = Recognizer.fromString("+", lexer, false)

    val add = object : Fold<AstNode, AstNode, AstNode> {
        override fun apply(stream: ParserStream, left: AstNode, right: AstNode): AstNode =
                OpNode(Op.Add, left, right)

        override fun leftInverse(result: AstNode): AstNode? =
                if (result is OpNode && result.op == Op.Add) result.args[0] else null

        override fun rightInverse(result: AstNode): AstNode? =
                if (result is OpNode && result.op == Op.Add) result.args[1] else null
    }

    val sub = object : Fold<AstNode, AstNode, AstNode> {
        override fun apply(stream: ParserStream, left: AstNode, right: AstNode): AstNode =
                OpNode(Op.Sub, left, right)

        override fun leftInverse(result: AstNode): AstNode? =
                if (result is OpNode && result.op == Op.Sub) result.args[0] else null

        override fun rightInverse(result: AstNode): AstNode? =
                if (result is OpNode && result.op == Op.Sub) result.args[1] else null
    }

    sum.set(
            product + ((plus.annotate(FormatOp.Infix) + product).fold(add) or (minus.annotate(FormatOp.Infix) + product).fold(sub))
                    .rep()
    )

    // Printing a generic Ast

    /* 1*2+3 */
    val genericAst0 = OpNode(
            Op.Add,
            OpNode(
                    Op.Mul,
                    NumNode(1),
                    NumNode(2)),
            NumNode(3))

    println("Pretty-Print: ${sum.print(genericAst0)}")

    /*  (1+2)*3 */
    val genericAst1 = OpNode(
            Op.Mul,
            OpNode(
                    Op.Add,
                    NumNode(1),
                    NumNode(2)),
            NumNode(3))


    println("Pretty-Print: ${sum.print(genericAst1)}")

    val stream = readLine()!!.createParserStream()
    val ast = sum.parse(stream)!!

    // now pretty-printTo the tree
    val sourceStream = StringOutStream()

    val printer = object : CstPrinter(sourceStream) {
        override fun print(tree: ConcreteSyntaxTree, annotation: Any?): CstPrinter =
                when (annotation) {
                    FormatOp.Infix -> append(" ").print(tree).append(" ")
                    else -> print(tree)
                }
    }

    val outTree = sum.print(ast)!!
    outTree.printTo(printer)
    println("Pretty-Print: $sourceStream")
}

enum class Op { Add, Sub, Mul, Div, Neg }

enum class FormatOp { Infix }

interface AstNode

class NumNode(val value: Int) : AstNode

class OpNode(val op: Op, vararg val args: AstNode) : AstNode

