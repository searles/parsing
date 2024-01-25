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

* `Recognizer`: Returns true if a token was consumed. Useful for all kinds of operators 
or keywords.

~~~ kotlin
    val plus: Recognizer = /* ... */ // consumes a +.
    
    if(plus.apply(stream)) {
        // plus was consumed from stream
    }
~~~

* `Parser<T>`: Returns the parsed item as an instance of `T`. They can be used
for identifiers or numbers, but also each parser rule is an instance of this 
interface. Parsers return null if they are not successful.

~~~ kotlin
    val num: Parser<Int> = /* ...*/ // parses a number
    
    val value = num.apply(stream)
    
    if(value != null) {
        // successfully parsed number
    }
~~~

* `Reducer<T, U>`: A reducer consumes the return value from the parser to its left 
(which is an instance of `T`) and returns an instance of `U`. Instances
of Reducers are usually used in a sequence or repetition. Like parsers, they
return `null` if they are unsuccessful. 

All parser types extend the `Recognizable`-interface. It can be used for a quick
syntax check.

Each parser requires one parameter, the `ParserStream`, a 
stream from which characters/tokens are fetched. It also 
provides information on the current position in the stream.

## Summary of parser combinators


| Call              | Description                                                                                      | Common Syntax |
|-------------------|--------------------------------------------------------------------------------------------------|---------------|
| `a or b`          | Choice of a or b. a and b must be of the same type.                                              | `a \| b`      |
| `a + b`           | a followed by b. a and b must not both be parsers.                                               | `a b`         |
| `a.rep()`         | Possibly empty repetition of a. a must be a Reducer<T, T>                                        | `a*`          |
| `a.rep1()`        | Non-empty repetition of a. a must be a Reducer<T, T>.                                            | `a+`          |
| `a.opt()`         | Optional of a. a must be a Reducer<T, T>.                                                        | `a?`          |
| `a.join(b)`       | For possibly empty sequences "b a b a ... b". a must be a Recognizer, b must be a Reducer<T, T>. | `(b (a b)*)?` |
| `a.join1(b)`      | Like `join` but for non-empty sequences "b a b a ... b".                                         | `b (a b)*`    |

There are further parser combinators but these should be the most important ones.

HINTS: 

* `Reducer.rep` does not support backtracking. Everything that can be consumed
by `rep` will be consumed, for both, `parse` and `print`. For this reason, printing
the result of `a.then(Reducer.rep(a))` where `a` is a `Reducer` will always fail.
The JoinReducer-class (created by `join`) or PlusReducer-class (`plus`) can be helpful 
to overcome this problem. 
* The properties of the 'then' and 'or'-combinator 
can be further modified using flags.

## Parser Functions

Apart from parsers there are functional interfaces that can be used to
introduce, convert or aggregate values.

* `Initializer<T>` introduces a value. `Initializer<T>` extends `Parser<T>`.
* `Mapping<T, U>` converts an instance of `T` to an instance of `U`. 
`Mapping<T, U>` extends `Reducer<T, U>`.
* `Fold<T, U, V>` combines an instance of `T` and `U` and returns an instance of `V`.
Using the `fold`-method, a `Parser<U>` is converted to a `Reducer<T, V>`.

## On inversion

All parser combinators are invertible. This means that eg `Parser<T>` not only
contains a `parser`-method but also a `print`-method that creates 
the source code. There are 
similar methods for `Reducer` and `Recognizer`. 

# Recursive Descent Parser

In the following, a simple recursive-descent parser is created that evaluates 
mathematical expressions. Inversion is not used here since the result 
will be the result of the calculation.
[The source code can be found here.](src/main/java/at/searles/at.searles.demo/DemoEval.kt)
 
## Number Parser

We want to parse natural numbers using the regex `[0-9]+`. 
To parse numbers we need 

* a lexer (or rather an instance of the `Tokenizer` interface). 

~~~ kotlin
    val lexer = Lexer()
~~~

If you want to ignore spaces and tabs you can create a `SkipTokenizer`
that wraps another tokenizer and ignores all tokens in its internal list.

~~~ kotlin
    val lexer = SkipTokenizer(Lexer())
    val wsTokenId = lexer.add(RegexParser.parse("[\t ]+"))
    lexer.addSkipped(wsTokenId)
~~~

* The token id of an integer match. In the lexer, all added regexes correspond
to a unique integer id. This id is used to check whether there is a match.

~~~ kotlin
    val numTokenId = lexer.add(RegexParser.parse("[0-9]+"))
~~~

* a mapping that converts a `CharSequence` to a number.

~~~ kotlin
    val numMapping = Mapping<CharSequence, Int> { 
        _, left -> Integer.parseInt(left.toString()) 
    }
~~~

The blank argument is the parser stream that can be
used eg to log the position in the stream.

* finally a parser that converts the matched characters to a number.

~~~ kotlin
    val num = Parser.fromToken(numToken, lexer, false, numMapping)
~~~

The boolean arguments is used to trigger that a match must not match
any other token in the lexer. This is useful 
to avoid that keywords are confused with identifiers.
 
## Recognizers, Concatenation and Reducers

Next, let's parse expressions like `1 + 2` using the following rule: 

~~~
sum: num '+' num ;
~~~

Both `num`-parsers return an integer-value. Since the second 
`num`-parser must consume the left hand side return value we need 
to convert it to a reducer using the fold-method: `num.fold(add)`. 
`add` is a simple binary function that adds the result of
the left hand and the current parser. 

~~~ kotlin
    val add = Fold<Int, Int, Int> { _, left, right ->
        left + right
    }
~~~

Furthermore, we need a recognizer for the plus symbol:

~~~ kotlin
    val plus = Recognizer.fromString("+", lexer, false)
~~~

Using the `then`-method we create a sequence of the `plus`-recognizer and
the `num.fold(add)`-reducer: `plus.then(num.fold(add))`. 

Prepending the first `num`-parser and pulling out the fold-method
(the latter just for aesthetic reasons) yields the following: 

~~~ kotlin
    val sum = num.then(plus.then(num).fold(add))
~~~

## Choice

Next, we introduce subtractions:

~~~
sum: num ('+' num | '-' num) ;
~~~

We will again need a recognizer for `'-'` and a fold-function to create
another reducer:

~~~ kotlin
    val minus = Recognizer.fromString("-", lexer, false)

    val sub = Fold<Int, Int, Int> { _, left, right ->
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
introduce a new rule that allows parentheses and requires such a recursive 
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
    val openPar = Recognizer.fromString("(", lexer, false)
    val closePar = Recognizer.fromString(")", lexer, false)

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

## Creating the stream

Now, there are all ingredients to finally evaluate mathematical expressions
using this simple grammar. All that is left is to create a `ParserStream`.

~~~
    val stream = ParserStream.fromString(readLine())

    println("Result = ${sum.parse(stream)}")
~~~

Entering an expression in a single line will now print the result:

~~~
1-(2+3)
Result = -4
~~~

In order to create a `ParserStream` out of a `Reader`, the class `ReaderCharStream`
can be used. It is very useful also for other purposes because it 
returns codePoints instead of `char` which might not contain a
full character due to UTF-16.

~~~ kotlin
    val stream = ParserStream(TokStream.fromCharStream(ReaderCharStream(reader)))`
~~~

The grammar enriched with multiplication, division and negation is
implemented in the file 
[`at.searles.at.searles.demo.DemoEval.kt`.](src/main/java/at/searles/at.searles.demo/DemoEval.kt)

# Inversion of parsers

For this part of the tutorial you need the "parsingtools"-project. 
[You find it here along with the sources.](https://github.com/searles/parsingtools)

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

The return value of the `print` method is an instance of 
`ConcreteSyntaxTree`. This class can be directly converted into a
string using `toString()` or it can be further formatted to apply
certain code rules. But let's first focus on the inversion itself
and introduce abstract syntax trees.

## Abstract Syntax Tree nodes

All nodes of the abstract syntax tree need a common supertype. For this purpose
we define the interface `AstNode`:

~~~ kotlin
interface AstNode
~~~

We need `AstNode`s for numbers (these are leafs) and for operator applications:

~~~ kotlin
enum class Op {Add, Sub, Mul, Div, Neg}

class NumNode(val value: Int): AstNode
class OpNode(val op: Op, vararg val args: AstNode): AstNode
~~~

## Inverting a Mapping

To invert mappings we need to implement the optional method `left`. 
The names of these method originate from `Reducer` which is implemented by 
`Mapping`. The method `left` must return `null` if the value in `result` is not
an image of the mapping, and otherwise undo the `parse`-method.

~~~ kotlin
    val numMapping = object: Mapping<CharSequence, AstNode> {
        override fun parse(stream: ParserStream, left: CharSequence): AstNode =
                NumNode(Integer.parseInt(left.toString()))

        override fun left(result: AstNode): CharSequence? = 
                if (result is NumNode) result.value.toString() else null 
    }
~~~

Invertible mappings usually look very similar to this implementation.

## Inverting a Fold

Folds are functions with a `left` and `right` argument. Thus, to invert it,
there are two functions, one to return the `left` argument and one for the `right`
argument.

~~~ kotlin
    val add = object: Fold<AstNode, AstNode, AstNode> {
        override fun apply(stream: ParserStream, left: AstNode, right: AstNode): AstNode =
            OpNode(Op.Add, left, right)

        override fun leftInverse(result: AstNode): AstNode? =
            if(result is OpNode && result.op == Op.Add) result.args[0] else null

        override fun rightInverse(result: AstNode): AstNode? =
            if(result is OpNode && result.op == Op.Add) result.args[1] else null
    }
~~~

All other folds are implemented exactly in the same way by replacing 
`Op.Add` by the corresponding operator.

## Pretty-printing an expression

We can test the pretty printer by parsing the input into an AST and then 
printing it. 

~~~ kotlin
    val ast = sum.parse(stream)
    println("Pretty-Print: ${sum.print(ast)}")
~~~

This will remove all unnecessary parentheses from the expression but keep the 
necessary ones:

~~~
(1+2)*(3+(-4))
Pretty-Print: (1+2)*(3+-4)
~~~

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

## Printing a ConcreteSyntaxTree

The return type of the `print`-method is a `ConcreteSyntaxTree`.

~~~ kotlin
    val outTree = sum.print(ast)!!
~~~

The `toString()`-method of this concrete syntax tree will return the source code
without applying formatting rules. Yet, using annotations in the
parser we can add markers to the concrete syntax tree
and use them for further formattions. 

The `print` method in `ConcreteSyntaxTree` is used for this
purpose. It takes an instance of the `CstPrinter`-class as a parameter.
In this `CstPrinter` we need to override a method to
apply our formatting rules. In this case, we want to add spaces to the left 
and right of the infix operators. The result should be a `String`, therefore
we print the result into a `StringOutStream`:

~~~ kotlin
    val sourceStream = StringOutStream()

    val printer = object: CstPrinter(sourceStream) {
        override fun print(tree: ConcreteSyntaxTree, annotation: Any): CstPrinter =
            when(annotation) {
                FormatOp.Infix -> append(" ").print(tree).append(" ")
                else -> print(tree)
            }
    }
~~~

After printing the concrete syntax tree, the final string
can be obtained from the `StringOutStream` using `toString()`.

~~~ kotlin
    val outTree = sum.print(ast)!!
    outTree.print(printer)
    println("Pretty-Print: $sourceStream")
~~~

[You find the pretty-printer at.searles.demo here.](src/main/java/at/searles/at.searles.demo/DemoInvert.kt)

In order to format source code with indentations or
more complex patterns, the 
`CstPrinter` must keep track of indentation levels. [A
concrete implementation for such a `CstPrinter` is given
in the unit test class `PrinterTest`.](src/test/java/at/searles/parsing/printing/test/PrinterTest.kt).

This concludes this tutorial. Suggestions, improvements and such are
always welcome. Enjoy this project. 

-- Karl
