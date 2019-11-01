# Invertible Parser Combinators

This project is implemented in Java and Kotlin. It provides parser combinators
that are invertible, ie, a parser using this project is also a pretty printer.

## In a nutshell

* Define recursive descent parsers using parser combinators, and you will get
    + A parser
    + A fast recognizer that verifies the syntax without creating a syntax tree (very useful for eg code style checkers)
    + A pretty printer
* Multiple lexers per parsers
    + Very fast lexer using one single FSA per lexer
* [work in progress] Utilities to generate java-class parsers from a text file with ANTLR-alike syntax

## Introduction

These Java classes provide parser combinators. A parser combinator is a function that combines
parsers in various ways (choice, sequence, repetition, ...) and they are widely used for instance
in Scala.

Writing a parser using parser combinators is very easy because the combinators correspond to
the operations that are available in a grammar. Yet a grammar definition for a parser can do
more - it is also a blue print for a pretty printer, a program that prints a text based on a grammar
definition.

This project therefore aims to do both: Define a grammar using parser generators and obtain a
pretty printer for free.

## Motivating Example

The motivating example is written in Kotlin although the parser combinators are written in Java8.

Writing a pretty printer can be difficult, if there are precedence rules. Consider the following
grammar (following the POSIX standard):

~~~
sum: product ('+' product)*
product: term ('*' term)*
term: num | '(' sum ')'
num: [0-9]+
~~~

The expressions `1 + 2 * 3` or `(1 + 2) * 3` are accepted by this grammar
with different syntax trees: 

~~~
 1 + 2 * 3 | (1 + 2) * 3
-----------+-------------
   +       |     *
  / \      |    / \
 1   *     |   +   3
    / \    |  / \
   2   3   | 1   2
~~~

In a pretty printer, it requires a complex case distinction to determine in what cases parentheses are needed.
Since this information is encoded in the grammar though, inverting the parser
fully takes care of this task.

There is some information available on how to proceed to use this project:

* [Tutorial](tutorial.md)
* [Demo of evaluating mathematical functions (kotlin)](src/main/java/at/searles/demo/DemoEval.kt)
* [Demo of inverting a parser (kotlin)](src/main/java/at/searles/demo/DemoInvert.kt)
* [HowTo: Some use cases for common situations](howto.md)
