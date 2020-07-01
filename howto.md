# How to - Various tweaks

This document will contain some tasks and show their solutions. They
can be sometimes a bit tricky. It will be extended on demand.

## Using multiple lexers

Lexers/Tokenizers can be mixed.


### Soft keywords

I have only recently learned that kotlin and scala allow keywords
like `public` as variable names while other "hard keywords"
like `if` are not. Such cases should be handled by the lexer. 
For this purpose, there is a 'minus' operation in regular expressions.

## Source Formatting

Creating an AST and printing it just for the sake of
source code formatting is some overkill. Yet,
ConcreteSyntaxTree (Cst) provides a convenient method
to format source code via parser annotations.

Using `TokStream.Listener` and `ParserStream.Listener`,
callbacks can be used to construct such a Concrete Syntax 
Tree just using the `recognize`-method in `Parser`, thus
avoiding the need to waste resources for constructing
an Ast. An additional benefit is that `TokStream.Listener`
also captures "hidden tokens" in case of `LexerWithHidden`.
This way, comments can also be formatted that would
otherwise be ultimately lost. Also redundant brackets are preserved.
Futhermore, using 
`FrameStream.Frame` the original position in the Cst
can be obtained.

Some unit tests in `PrinterTest` use this method. The
`actFormat`-method shows a possible implementation:

~~~ kotlin
val stack: Stack<ArrayList<ConcreteSyntaxTree>> = Stack()

stack.push(ArrayList())

this.stream.setListener(object: ParserStream.Listener {
    override fun <C : Any?> annotationBegin(annotation: C) {
        // each annotation will create a new branch
        stack.push(ArrayList())
    }

    override fun <C : Any?> annotationEnd(annotation: C, success: Boolean) {
        if(!success) {
            // we created the list for nothing. Remove it.
            stack.pop()
            return
        }

        // otherwise, add it.
        val list = stack.pop()
        val cstNode = ListConcreteSyntaxTree(list)
        stack.peek().add(AnnotatedConcreteSyntaxTree(annotation, cstNode))
    }
})

this.stream.tokStream().setListener(object: TokStream.Listener {
    override fun tokenConsumed(tokId: Int, frame: FrameStream.Frame) {
        // skip all white spaces
        if(tokId == whiteSpaceTokId) {
            return
        }

        // add all other tokens to current top in stack.
        stack.peek().add(
                LeafConcreteSyntaxTree(frame.toString())
        )
    }
})

// call recognize. There will be one single item
// left on the stack.
if(!parser.recognize(stream)) {
    output = null
    return
}

val cst = ListConcreteSyntaxTree(stack.pop())
~~~ 

## Syntax highlighting

Syntax highlighting happens usually on a per-token-base.
Yet, here tokens are detected in combination
with the parser since multiple lexers can be used
or a substring can match multiple token patterns (eg
the string "if" could be an identifier AND a keyword).

Thus, for syntax highlighting, it is recommended to
use the `recognize`-method of the parser in 
combination with a `TokStream.Listener`. Using
the `frame`-parameter and the `tokId`, it can be
checked which token pattern is matched (including 
hidden tokens if `LexerWithHidden` is used).

## How to detect EOF

In the underlying CharStream, EOF is represented by -1.
The (integer-based) lexer interprets -1 like any other 
integer, hence we can add -1 as a token and detect it.
This is already done in `Recognizer.eof(tokenizer: Tokenizer)`.
Thus, in order to check whether all characters of the parser stream have 
been consumed, simply check whether the eof-recognizer succeeds.

~~~ kotlin
    val stream: ParserStream = /* ... */
    val eof = Recognizer.eof(lexer)
    
    // ... do parse
    if(eof.recognize(stream)) {
        // End of file
    }
~~~

For a concrete implementation, consult the 
[eof unit test](src/test/java/at/searles/parsing/EofTest.kt)

## Using Lists

The `Utils`-class provides some methods to create lists:

* `Utils.empty()` creates an empty list
* `Utils.singleton(parser)` creates a parser that adds the 
return value of the argument `parser` to a new list.
* `Utils.append(parser, minLeftElements)` creates a
reducer that appends the return value of `parser` to the 
list that is passed on by the left hand side parser. 
`minLeftElements` states, how many elements the lhs parser
will pass at least which is needed for inversion.
* `Utils.binary(parser)` creates a reducer that adds
the return value of the left parser and of `parser` to a new 
list.
* `Utils.list(parser[, separator])` creates a Parser that
repeatedly applies `parser` and adds all return values to a list.
The return value is possibly empty, a Recognizer as a separator
(eg for comma separated  values) is optional.
* `Utils.list1(parser[, separator])` like `list` but with
at least one value.

### Add items to list if sequence contains more than one element

Consider `a a*` where `a: Parser<Tree>` is some parser returning an 
instance of some class `Tree`.
If there is only one `a`, its return value should be passed
through. If there are two or more `a`s, then they should be
collected in a list and a new node should be returned.

So the scenario is something like this.
~~~
a     -> a
a a   -> Tree(List(a,a))
a a a -> Tree(List(a,a,a))
~~~

If the tree would allow just binary nodes, it would be easy:

~~~ kotlin
// createBinaryNode is a Fold<Tree, Tree, Tree>

a.then(Reducer.rep(a.fold(createBinaryNode)))
~~~

But in this case, we want to collect all child nodes in a list,
provided there are at least two such nodes.

#### Solution

If there is more than one element, we can add the second element
to a new list using `Utils.binary` and add further elements using
`Utils.append`. The grammar rule in pseudo code then looks as 
follows:

~~~
a (`binary(a)` (`append(a, 2)`)* createNode)?
~~~

In code it becomes the following

~~~ kotlin
a.then(
    Reducer.opt(
        Utils.binary(a)
        .then(
            Reduce.rep(
                Utils.append(a, 2)
            )
        )
        .then(createNode)
    )
)
~~~

### Add items to list if sequence contains more than one element in comma separated list

This task resembles the previous one but now all values are separated 
by a comma: `a (',' a)*`. We can use the same approach as before,
but alternatively, we can create a singleton list after the first
comma and add the remaining items using `Utils.append` and `joinPlus`.
Singleton is a mapping in `at.searles.utils.list`.

In pseudo code:

~~~
a (',' `Singleton()` joinPlus[',', append(a, 1)])?
~~~


And in kotlin (`comma` is a Recognizer for `','`):

~~~ kotlin
a.then(
    Reducer.opt(
        comma.then(
            SingletonList()
            .then(
                comma.joinPlus(
                    Utils.append(a, 1)
                )
            )
        )
    )
)
~~~

## Using Maps

TODO

## Generic Data Objects

Assume you have a class 
`class Person(val name: String, age: Int, profession: String) {}`
and you want to create instances of `Item` using a parser.
In the following, `num: Parser<Int>` is a parser that
parses a number and `id: Parser<String>` is a parser that parses
an identifier.

~~~
person: name ',' age ',' profession ;
name: id ;
age: num ;
profession: id ;
~~~  

Creating a person can be done using the methods
`Utils.put` and `Utils.create`.

// TODO

# Experimental: Collect all possible parses

This is about ParserAndParser.

// TODO