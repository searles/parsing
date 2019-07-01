# Invertible Parser Combinators

## In a nutshell

* Define LL(1) parsers using parser combinators, and you will get
    + A parser
    + A fast recognizer that verifies the syntax without creating a syntax tree (very useful for eg code style checkers)
    + A pretty printer
* Multiple lexers per parsers
    + Very fast lexer using one single FSA per lexer
* Utilities to generate java-class parsers from a text file with ANTLR-alike syntax

## Introduction

These Java Classes provide parser combinators. A parser combinator is a function that combines
parsers in various ways (options, sequence, repetition) and they are widely used (for instance
in Scala).

Writing a parser using parser combinators is very easy because the combinators correspond to
the operations that are available in a grammar. Yetm a grammar definition for a parser can do
more - it is also a blue print for a pretty printer, a program that prints a text based on a grammar
definition.

This project therefore aims to do both: Define a grammar using parser generators and obtain a
pretty printer for free.

## Motivating Example

The motivating example is written in Kotlin although the parser combinators are written in Java8.

Writing a pretty printer can be difficult, if there are precedence rules. Consider the following
grammar (following the POSIX standard):

~~~
expr: sum
sum: factor ('+' factor)*
factor: term ('*' term)*
term: num | '(' expr ')'
num: NUM
NUM: [0-9]+
~~~

It accepts sentences like `1 + 2 * 3` or `(1 + 2) * 3`. If this is represented by a syntax tree, both
these expressions are represented differently:

~~~
1 + 2 * 3

  +
 / \
1   *
   / \
  2   3
~~~

~~~
(1 + 2) * 3

    *
   / \
  +   3
 / \
1   2
~~~

In a pretty printer, it requires a complex case distinction to determine whether brackets are needed and around which
expression. Using this project, the parser can be inverted, ie, it provides a method that returns a correct
representation of the given syntax tree.

### Tokens

In order to create this parser, we need to define the `NUM`-Token

~~~
NUM: [0-9]+
~~~

We can add such tokens to the lexer:

~~~ kotlin
val lexer = Lexer() // Create lexer
val NUM = lexer.add(RegexParser.parse("[0-9]+")) // Add regex to lexer
~~~

### Functions

We can define a function `toInt` that parses this `CharSequence` and returns the parsed integer.

~~~ kotlin
Mapping<CharSequence, Integer> toInt = seq -> parseInteger(seq); // Create toInt function
~~~

To use the inversion capabilities of these parser combinators, this function must also
define its inverse:

~~~
Mapping<CharSequence, Integer> toInt = new Mapping<>() {
    // TODO
}
~~~

### Reducer combinator

The `toInt` function can be combined with the token parser:

~~~
num: NUM ~ toInt
NUM: [0-9]+
~~~

The operand `~` ensures that the second function is applied to the result of the first parser.
The result of this parser thus is an integer. The `~` operand is represented by the `reduce(toInt)` method.

~~~
Parser<Integer> num = Parser.token(NUM).reduce(toInt); // NUM ~ toInt
~~~

And that is it.

### ParserStream

TODO

### The full parser for a number token

In case of a syntax errors, an instance of the Environment interface must be passed to the
parser call. It contains one simple function that is called in case of a parsing error.

The following few lines apply this parser to all lines that are read from stdin.

~~~ java
~~~

### Syntax trees

We now want to finish writing the grammar so that it returns a syntax tree.

For this, let's first define a data type `Tree`: A `Tree` is either a leaf containing
a number or a branch with two children containing a function symbol.

~~~ java
public interface Tree {
}

public class Leaf implements Tree {
    private final int value;

    public Leaf(int value) {
        this.value = value;
    }
}

public class Branch implements Tree {
    private final String op;
    private final Tree left;
    private final Tree right;

    public Branch(String op, Tree left, Tree right) {
        // TODO
    }

}
~~~

## Types

~~~
Recognizer:  ->
Parser:      -> A
Reducer:   A -> B

Initialize:       -> A
Convert:        A -> B
Consume:        A ->
Aggregate:   A, B -> C
~~~


# Parser
