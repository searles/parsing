# Tutorial (in Kotlin)

# Parser Combinators

A certain familiarity with regular expressions and basic
grammar syntax is expected in the following.

Parser combinators are used eg in Scala. You define simple parsers that return
numbers, identifiers etc... and then you combine them using various operations
like sequencing, choice, repetition etc... Basically, you combine parsers
to obtain more complex parsers. As always, [Wikipedia knows more](https://en.wikipedia.org/wiki/Parser_combinator).

In this project, there are three basic types of parsers, and combining them will
return one such kind of parser:

* `Recognizer`: Returns true if a token was consumed. Useful for all kinds of operands or keywords.

~~~ kotlin
    val plus: Recognizer = /* ... */ // consumes a +.
    
    if(plus.apply(env, stream)) {
        // plus was consumed from stream
    }
~~~

* `Parser<T>`: Returns the parsed item as an instance of `T`. They can be used
for identifiers or numbers, but also each parser rule is an instance of this 
interface. Parsers return null if they are not successful.

~~~ kotlin
    val num: Parser<Int> = /* ...*/ // parses a number
    
    val value = num.apply(env, stream)
    
    if(value != null) {
        // successfully parsed number
    }
~~~

* `Reducer<T, U>`: A reducer consumes the return value from the parser to its left 
(which is an instance of `T`) to its left and returns an instance of `U`. Instances
of Reducers are usually used in a sequence or repetition. Like parsers, they
return `null` if they are unsuccessful. 

All parser types extend the `Recognizable`-interface. It can be used for a quick
syntax check.

Each parser at least two parameters:

* `Environment`: A context object that is used to propagate notifications of 
parsing errors.
* `ParserStream`: A stream from which characters/tokens are fetched. It also 
provides information on the current position in the stream.

## Summary of parser combinators


| Call              | Description                                                                                      | Syntax |
|-------------------|--------------------------------------------------------------------------------------------------|--------|
| `a.or(b)`         | Choice of a or b. a and b must be of the same type.                                              | a | b  |
| `a.then(b)`       | a followed by b. a and b must not both be parsers.                                               | a b    |
| `Reducer.rep(a)`  | Possibly empty repetition of a. a must be a Reducer<T, T>                                        | a*     |
| `Reducer.plus(a)` | Non-empty repetition of a. a must be a Reducer<T, T>.                                            | a+     |
| `Reducer.opt(a)`  | Optional of a. a must be a Reducer<T, T>.                                                        | a?     |
| `a.join(b)`       | For possibly empty sequences "a b a b ... a". a must be a Recognizer, b must be a Reducer<T, T>. |        |

There are further parser combinators but these should be the most important ones.

## Parser Functions

Apart from parsers there are functional interfaces that can be used to
introduce, convert or aggregate values.

* `Initializer<T>` introduces a value. `Initializer<T>` extends `Parser<T>`.
* `Mapping<T, U>` converts an instance of `T` to an instance of `U`. 
`Mapping<T, U>` extends `Reducer<T, U>`.
* `Fold<T, U, V>` combines an instance of `T` and `U` and returns an instance of `V`.
Using the `fold`-method, a `Parser<U>` is converted to a `Reducer<T, V>`.

## On inversion

All parser combinators are invertable. This means that eg `Parser<T>` not only
contains a `parser`-method but also a `print`-method that creates a `StringTree`
out of the result of the `print`-method. There are similar methods for `Reducer` 
and `Recognizer`. 

# Recursive Descent Parser

In the following, a simple recursive-descent parser is created that evaluates 
mathematical expressions. Inversion is not used here since the result 
will be the result of the calculation.
[The source code can be found here.](src/main/java/at/searles/demo/DemoEval.kt)
 
## Number Parser

We want to parse natural numbers using the regex `[0-9]+`. 
To parse numbers we need 

* a lexer (or rather an instance of the `Tokenizer` interface). 

~~~ kotlin
    val lexer = Lexer()
~~~

* a token pattern that accepts an integer number.

~~~ kotlin
    val numToken = lexer.token(RegexParser.parse("[0-9]+"))
~~~

* a mapping that converts a `CharSequence` to a number.

~~~ kotlin
    val numMapping = Mapping<CharSequence, Int> { 
        _, left, _ -> Integer.parseInt(left.toString()) 
    }
~~~

* finally a parser that converts the matched characters to a number.

~~~ kotlin
    val num = Parser.fromToken(numToken, numMapping, false)
~~~

If the last argument is true, then a token is only accepted if no other
token in the lexer matches (mixing lexers is allowed). This is useful 
to avoid that keywords are confused with identifiers.

## Recognizers, Concatenation and Reducers

Next, let's combine multiple `num`-parsers using the following rule: 

~~~
sum: num '+' num ;
~~~

We need a recognizer for the plus symbol:

~~~ kotlin
    val plus = Recognizer.fromString("+", lexer, true)
~~~

Using the `then`-method we can now create a sequence of the `plus`-recognizer and
the `num`-parser: `plus.then(num)`. This sequence is an instance of `Parser<Int>`.
Since this parser must also consume the return value of the first `num`-parser,
we have to apply the `fold`-method to create a `Reducer<Int, Int>`:

~~~ kotlin
    val sum = num.then(plus.then(num).fold(add))
~~~

`add` is a simple binary function that sums up the return values of the 
left `num` and the right `num` parser.

~~~ kotlin
    val add = Fold<Int, Int, Int> { _, left, right, _ ->
        left + right
    }
~~~

## Choice

Next, we introduce subtractions:

~~~
sum: num ('+' num | '-' num) ;
~~~

We will again need a recognizer for `'-'` and a fold-function to create
another reducer:

~~~ kotlin
    val minus = Recognizer.fromString("-", lexer, true)

    val sub = Fold<Int, Int, Int> { _, left, right, _ ->
        left - right
    }
~~~

The reducer for subtractions is `minus.then(num).fold(sub)`. Using
the `or` method we finally obtain the following:

~~~
    val sum = num.then(
        plus.then(num).fold(add)
        .or(minus.then(num).fold(sub))
    )
~~~

## Repetition

In order to allow multiple additions and subtractions we need repetitions:

~~~
sum: num ('+' num | '-' num)* ;
~~~

Repetition only work on recognizers and reducers with the same left- and 
return type (`Reducer<T, T>`) since the return value of the reducer is
used as left input by subsequent reducers. 

~~~ kotlin
    val sum = num.then(
        Reducer.rep(
            plus.then(num).fold(add)
            .or(minus.then(num).fold(sub))
        )
    )
~~~

`rep` creates possibly empty repetitions, `plus` are non-empty repetitions
and `opt` is an optional. An alternative to optionals is using `or` where
the second argument is a simple `Mapping` (which is also an instance of `Reducer`).

## Recursion

Context-free grammars allow recursive definitions. In the following we
introduce a new rule that allows parentheses and require such a recursive 
definition:

~~~
term: num | '(' sum ')' ;
sum: term ('+' term | '-' term)* ;
~~~

In order to reference the `sum` parser before it is defined, we can use `Ref`,
a reference to a parser that is initialized only later. 

~~~ kotlin
    val sum = Ref<Int>("sum")
~~~

The label provided in the constructor has no
specific function except for providing a useful return value for the
`toString()`-method. This is useful even for non-recursive rules for
debugging purposes.

Using `Ref.set`, the referenced parser is set.

~~~ kotlin
    val openPar = Recognizer.fromString("(", lexer, true)
    val closePar = Recognizer.fromString(")", lexer, true)

    val term = num.or(
        openPar.then(sum).then(closePar)
    )
    
    sum.set(
        term.then(
            Reducer.rep(
                plus.then(term).fold(add)
                .or(minus.then(term).fold(sub))
            )
        )
    )
~~~

`Ref`s can be added to a parser also using the `ref`-method. In `DemoEval.kt`, all 
parsers use a reference to achieve a simpler output of the parser's 
`toString`-method which is very useful for debugging.

## Error handling

Each recognizer, parser and reducer requires an instance of `Environment`. 
In case of a mismatch in a sequence of parsers (thus an instance of 
`Recognizable.Then`), its `notifyNoMatch` is called. 

If the grammar is supposed to be LL-1, it is
best to throw an expeption, otherwise backtracking is used to recover from
this mismatch. Our grammar is LL-1, thus we throw an exception.

~~~ kotlin
    val env = Environment { stream, failedParser ->
        throw ParserException(
            "Error at ${stream.offset()}, expected ${failedParser.right()}"
        )
    }

class ParserException(msg: String) : RuntimeException(msg)
~~~

## Creating the stream

Now, there are all ingredients to finally evaluate mathematical expressions
using this simple grammar. All that is left is to create a `ParserStream`.

~~~
    val stream = ParserStream.fromString(readLine())

    println("Result = ${sum.parse(env, stream)}")
~~~

Entering an expression in a single line will now print the result:

~~~
1-(2+3)
Result = -4
~~~

In order to create a `ParserStream` out of a `Reader`, the class `ReaderCharStream`
can be used. It is very useful because it returns codePoints instead of `char`.

~~~ kotlin
    val stream = ParserStream(TokStream.fromCharStream(ReaderCharStream(reader)))`
~~~

The grammar enriched with multiplication, division and negation is
implemented in the file `at.searles.demo.DemoEval.kt`.

# Inversion of parsers (at.searles.demo.DemoInvert.kt)

Parsers are usually used to create an abstract syntax tree (AST) out of 
their input. A pretty printer does the exact opposite: It creates source
code out of an abstract syntax tree.

The main motivation to invert a parser is that pretty printers 
are far from trivial to implement (because of eg operator precedence
and parentheses). Creating a pretty printer directly out of a parser 
thus saves a lot of time.

In this example, the grammar of the previous section will be modified so
that the parser creates an abstract syntax tree (AST) and the printer 
creates source code. Since
all parser combinators already contain a `print` method that is the inverse
of the `parse`-method, this mainly requires to modify the `Mapping`s and `Fold`s
and add (partial) inverse methods to them.

## AstNodes

The class `AstNode` provides nodes of an AST. It requires the ParserStream 
in its constructor which is used to store the position in the stream.

We need `AstNode`s for numbers, for unary nodes and for binary nodes:

~~~ kotlin
enum class Op {Add, Sub, Mul, Div, Neg}

class NumNode(stream: ParserStream, val value: Int): AstNode(stream)

class UnNode(stream: ParserStream, val op: Op, val arg: AstNode): AstNode(stream)
class BinNode(stream: ParserStream, val op: Op, val arg0: AstNode, val arg1: AstNode): AstNode(stream)
~~~

## Inverting a Mapping

To invert mappings we need to implement the optional method `left`. 
The names of these method originate from `Reducer` which is implemented by 
`Mapping`. The method `left` must return `null` if the value in `result` is not
an image of the mapping, and otherwise undo the `parse`-method.

~~~ kotlin
    val numMapping = object: Mapping<CharSequence, AstNode> {
        override fun parse(env: Environment, left: CharSequence, stream: ParserStream): AstNode =
                NumNode(stream, Integer.parseInt(left.toString()))

        override fun left(env: Environment, result: AstNode): CharSequence? = 
                if (result is NumNode) result.value.toString() else null 
    }
~~~

Invertible mappings usually look very similar to this implementation.

## Inverting a `Fold`

Folds are functions with a `left` and `right` argument. Thus, to invert it,
there are two functions, one to return the `left` argument and one for the `right`
argument.

~~~ kotlin
    val add = object: Fold<AstNode, AstNode, AstNode> {
        override fun apply(env: Environment, left: AstNode, right: AstNode, stream: ParserStream): AstNode =
            BinNode(stream, Op.Add, left, right)

        override fun leftInverse(env: Environment, result: AstNode): AstNode? =
            if(result is BinNode && result.op == Op.Add) result.arg0 else null

        override fun rightInverse(env: Environment, result: AstNode): AstNode? =
            if(result is BinNode && result.op == Op.Add) result.arg1 else null
    }
~~~

All other folds are implemented exactly in the same way by replacing the operand 
`Op.Add`.

## Pretty-printing an expression

We can test the pretty printer by parsing the input into an AST and then 
printing it. 

~~~ kotlin
    val ast = sum.parse(env, stream)
    println("Pretty-Print: ${sum.print(env, ast)}")
~~~

This will remove all unnecessary parentheses from the expression but keep the 
necessary ones:

~~~
(1+2)*(3+(-4))
Pretty-Print: (1+2)*(3+-4)
~~~

The print-method also uses the `env`-object to propagate if backtracking
is necessary when printing failed which might indicate a performance issue.

## Adding annotations for custom formatting

Sometimes it is desirable to add white spaces to the output. 
In our parser, all infix operations should be wrapped by spaces to
improve readability. For this purpose we annotate the recognizers, which
is shown here for the product-rule:

~~~ kotlin
    val product = literal.then(
            Reducer.rep(
                times.annotate(Annotation.Infix).then(literal).fold(multiply)
                .or(slash.annotate(Annotation.Infix).then(literal).fold(divide))
            )
    ).ref("product")

enum class Annotation { Infix }
~~~

## Printing a StringTree

The return type of the `print`-method is a `StringTree` which contains the 
concrete syntax tree of the output. 

~~~ kotlin
    val outTree = sum.print(env, ast)!!
~~~

THe `toString()`-method of `StringTree` will return the source code
without applying formatting rules. 

In order to use the annotations of the previous
section, the `toStringBuilder`-method should be used. This method uses
a function to decorate an annotated `StringTree` with additional
formattings. Here, we add spaces to the left and right. 

~~~ kotlin
    val outTree = sum.print(env, ast)!!

    val formatting = BiFunction<Any, StringTree, StringTree> {
        category, tree -> if(category == Annotation.Infix) tree.consLeft(" ").consRight(" ") else tree
    }

    val formattedSource = outTree.toStringBuilder(StringBuilder(), formatting) 

    println("Pretty-Print: $formattedSource")
~~~

`StringTree` itself is a very simple functional interface. It can be
easily implemented in order to obtain more complex formattings like
indentations or optional line breaks.

[You fine the pretty-printer demo here.](src/main/java/at/searles/demo/DemoInvert.kt)

This concludes this tutorial. Enjoy this project. 