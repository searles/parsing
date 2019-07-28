# How to use the Utils-class

This document will contain some tasks and show their solutions. They
can be sometimes a bit tricky. It will be extended on demand.

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

## Generic Data Objects

In the following, let `num: Parser<Int>` be a parser that
parses a number and `id: Parser<String>` a parser that parses
an identifier.

### Single data object

