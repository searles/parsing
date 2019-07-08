# Tutorial (in Kotlin)

# Parser Combinators

A certain familiarity with regular expressions and basic
grammar syntax is expected in the following.

A main inspiration for this project were parser combinators the way they
are used in Scala. Basically, you combine parsers
to obtain more complex parsers. As always, [Wikipedia knows more](https://en.wikipedia.org/wiki/Parser_combinator).

Each parser combinator accepts two or more parameters:

* `Environment`: A context object that is used to propagate notifications of 
parsing errors.
* `ParserStream`: A stream from which characters/tokens are fetched. It also 
provides information on the current position in the stream.

There are three basic parser types in this project:

* `Recognizer`: Returns true if a token was consumed. Useful for all kinds of operands or keywords.
* `Parser<T>`: Returns the parsed item as an instance of `T`. Use this for numbers, identifiers etc...
* `Reducer<T, U>`: A reducer consumes the return value from the parser (which 
is an instance of `T`) to its left and returns an instance of `U`. Doing this
it may consume further elements from the input stream.

Every return value of a parser or reducer must be consumed unless
it returns `null` which indicates an unsucessful parse attempt.

All parser types contain additional methods to combine parsers and also to invert it. Additionally, there are function interfaces that allow to initialize 
values (`Initializer<T>`), convert 
them (`Mapping<T, U>`) or to combine them with other vales 
(`Fold<T, U, V>`).

All parser types extend the `Recognizable`-interface. It can be used to
simply check whether an expression is accepted.

# Recursive Descent Parser (at.searles.demo.DemoEval.kt)

In the following, a simple recursive-descent parser is created that evaluates 
mathematical expressions. Inversion is not used here since the result 
will be the result of the calculation. 

## Number Parser

We want to parse natural numbers using the regex `[0-9]+`. 
To parse numbers we need 

* a lexer. The lexer uses a single FSA, thus it is very fast.

~~~ kotlin
    val lexer = Lexer()
~~~

* a token pattern that accepts an integer number.

~~~ kotlin
    val numToken = lexer.token(RegexParser.parse("[0-9]+"))
~~~

* a mapping that converts the `CharSequence` that matched this pattern, 
to a number since Parsers
that match patterns will return a `CharSequence`.

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

The following creates arecognizer for `+`.

~~~ kotlin
    val plus = Recognizer.fromString("+", lexer, true)
~~~

`plus.then(num)` is a combination of a `Recognizer` and a `Parser<Int>` which
is also an instance of `Parser<Int>`. Yet, `'+' num` must be an instance of `Reducer<Int, Int>` since it
must consume the left `num`'s return value. In order to create a
reducer out of a parser, we need an instance of `Fold` that consume
the return value of the left parser and the one of the parser itself:

~~~ kotlin
    val add = Fold<Int, Int, Int> { _, left, right, _ ->
        left + right
    }
~~~

Finally the rule looks as follows:

~~~
    val sum = num.then(plus.then(num).fold(add))
~~~

## Choice

Let's expand the rule by subtractions: 

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

And we can use the `or` method to combine the two reducers which results
again in a reducer.

~~~
    val sum = num.then(
        plus.then(num).fold(add)
        .or(minus.then(num).fold(sub))
    )
~~~

## Repetition

Normally, we would allow expressions like `1+2-3+4` of arbitrary length. 
For this purpose, we add one `*` to the rule:

~~~
sum: num ('+' num | '-' num)* ;
~~~

Repetition only work on recognizers and reducers with the same left- and 
return type (`Reducer<T, T>`). 

~~~ kotlin
    val sum = num.then(
        Reducer.rep(
            plus.then(num).fold(add)
            .or(minus.then(num).fold(sub))
        )
    )
~~~

Optionals can be created the same way using `Reducer.opt(...)`.

## Recursion

Let's also allow expressions in parentheses. For this purpose, we add one new
rule.

~~~
term: num | '(' sum ')' ;
sum: term ('+' term | '-' term)* ;
~~~

In order to reference the `sum` parser before it is defined, we use a 
an instance of `Ref`. The label provided in the constructor has no
specific function except for providing a useful return value for the
`toString()`-method:

~~~ kotlin
    val sum = Ref<Int>("sum")
    
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

Refs are also useful to obtain a more readable debugging output. In
`DemoEval.kt`, all parsers use a reference for that purpose.

## Error handling

Each recognizer, parser and reducer requires an instance of `Environment`. In
case of a mismatch, its `notifyNoMatch` is called. Parameters are the stream 
and the failed parser. The failed parser is always a sequence of two parsers,
thus an instance of `Recognizable.Then`. The left item was matched while the
right one was not matched. If the grammar is supposed to be LL-1, it is
best to throw an expeption, otherwise backtracking is used to recover from
this mismatch. In this example, an exception is thrown.

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
using this simple grammar. 

~~~
    val stream = ParserStream.fromString(readLine())

    println("Result = ${sum.parse(env, stream)}")
~~~

Entering an expression in a single line will now print the result:

~~~
1-(2+3)
Result = -4
~~~

The grammar enriched with multiplication, division and negation is
implemented in the file `at.searles.demo.DemoEval.kt`.

# Inversion of parsers (at.searles.demo.DemoInvert.kt)

The main motivation to invert a parser is that pretty printers 
are far from trivial to implement.
One of the reasons for this is operator precedence that sometimes
makes it necessary to add parentheses around an expression. Achieving this
programmatically can be very difficult and results in hard-to-read code. 
Though much work can be saved because all the information which expressions 
need to be put into parentheses is already encoded in the grammar. Inverting
the parser will thus put exactly those expressions into parentheses for which
this is necessary.

In this section, the grammar of `DemoEval` is reused which is a slight
extension of the grammar in the previous section.

Parsers are usually used to create an abstract syntax tree (AST) out of 
their input. A parser creates textual output out of an abstract syntax tree, 
thus a pretty printer is the exact inverse of the parser. In the following,
the grammar is modified so that it can be inverted. This mainly requires
to modify the `Mapping`s and `Fold`s.

## AstNodes

The class `AstNode` provides nodes of an AST. It requires the ParserStream 
in its constructor which is used to store the position in the stream.

There is a class for number nodes, one for unary nodes and one for binary nodes:

~~~ kotlin
enum class Op {Add, Sub, Mul, Div, Neg}

class NumNode(stream: ParserStream, val value: Int): AstNode(stream)

class UnNode(stream: ParserStream, val op: Op, val arg: AstNode): AstNode(stream)
class BinNode(stream: ParserStream, val op: Op, val arg0: AstNode, val arg1: AstNode): AstNode(stream)
~~~

## Inverting a `Mapping`

Mappings become invertible by overriding the optional method `left`. This method
should be the exact opposite of the `parse`-method. The names of these method
originate from `Reducer` which is implemented by `Mapping`.

`left` must return `null` if the value in `result` cannot be inverted by
the Mapping.

~~~ kotlin
    val numMapping = object: Mapping<CharSequence, AstNode> {
        override fun parse(env: Environment, left: CharSequence, stream: ParserStream): AstNode =
                NumNode(stream, Integer.parseInt(left.toString()))

        override fun left(env: Environment, result: AstNode): CharSequence? = 
                if (result is NumNode) result.value.toString() else null 
    }
~~~

This is the usual blue print of invertible Mappings.

## Inverting a `Fold`

Folds are functions with a `left` and `right` argument. Thus, to invert it,
there are two functions, one for `left` and one for `right`.

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

This will remove all unnecessary parentheses but keep the necessary ones:

~~~
(1+2)*(3+(-4))
Pretty-Print: (1+2)*(3+-4)
~~~

The print-method also uses the `env`-object to propagate if backtracking
is necessary when printing failed. If printing should be possible with back-tracking, 
the optional method `notifyLeftPrintFailed` should be overridden.

## Adding annotations for custom formatting

In most cases, white spaces should be added to the output to format it. This can 
be done by annotating parsers for which formatting is desired.
In our parser, all infix operations should be wrapped by one single space to
improve readability. For this purpose we add an enum with so far one single category:

~~~ kotlin
enum class Annotation { Infix }
~~~

In the parser rules the infix operands can now be annotated with this category.
The parser rule for multiplications and divisions now looks as follows.

~~~ kotlin
    val product = literal.then(
            Reducer.rep(
                times.annotate(Annotation.Infix).then(literal).fold(multiply)
                .or(slash.annotate(Annotation.Infix).then(literal).fold(divide))
            )
    ).ref("product")
~~~

The final ref is used solely to provide a better readable debugging output.

## Printing

The return type of the `print`-method is a `StringTree` which resembles a 
concrete syntax tree. Its `toString()`-method will return the source code
without applying formatting rules. 

~~~ kotlin
    val outTree = sum.print(env, ast)!!
~~~

To use the annotations of the previous
section the `toStringBuilder`-method should be used. In this method,
annotations are resolved by calling the functional argument. In this argument,
the annotated `StringTree` can be decorated. 

~~~ kotlin
    val outTree = sum.print(env, ast)!!

    val formattedSource = outTree.toStringBuilder(StringBuilder()) {
        category, tree -> if(category == Annotation.Infix) tree.consLeft(" ").consRight(" ") else tree
    }

    println("Pretty-Print: $formattedSource")
~~~

`StringTree` itself is a very simple functional interface. By implementing
the `toStringBuilder`-method, more complex formattings like indentations
can be achieved.