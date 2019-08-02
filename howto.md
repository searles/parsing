# How to - Various tweaks

This document will contain some tasks and show their solutions. They
can be sometimes a bit tricky. It will be extended on demand.

## How to detect EOF

In the underlying CharStream, EOF is represented by -1.
The (integer-based) lexer interprets -1 like any other 
integer, hence we can add -1 as a token and detect it.
This is already done in `Recognizer.eof(tokenizer: Tokenizer)`.
Thus, in order to check whether the parser stream has fully
been consumed, simply check whether the eof-recognizer succeeds.

~~~ kotlin
    val stream: ParserStream = /* ... */
    val eof = Recognizer.eof(lexer)
    
    // ... do parse
    if(eof.recognize(env, stream)) {
        // End of file
    }
~~~

Be a bit cautious and make sure that hidden tokens (if there
are any) have fully been consumed. This is usually the case
if `eof` is proceeded by failed parse attempt, eg
after a `rep`-Reducer (they must fail eventually).

For a concrete implementation, consult the 
[eof unit test](src/test/java/at/searles/parsing/test/EofTest.kt)

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
`Utils.setter` and `Utils.builder`.

For this purpose, we need a class that contains all required members
as public members (here it is shown in Java).
Right after creation all of these member variables
must be `null`, indicating that they have not been set.

~~~ java
public class PersonBuilder /* ... */ {
    public String name = null;
    public Integer age = null;
    public String profession = null;
    
    // ...
}
~~~

In order to use `Utils.builder`, this class must contain
the default constructor. For inversion, 
it also must contain a method `boolean isEmpty()` (the inverse
of the default constructor) that returns true if the object 
looks like it has been created (ie all members are null).

In order to use `Utils.setter`, the object must furthermore
contain a method `PersonBuilder copy()` that clones
the object. This method is needed because 
every time the object is passed, it must be kept because
of backtracking.

The abstract class `GenericStruct<A>` already provides both these methods,
so in general, simply extend this class. If you need
deep cloning or a different implementation of `isEmpty`, simply
implement these two methods. 
 
Since an instance of `PersonBuilder` is not a person, we
also can use the `Utils.build`-method. This method
calls a method `build` to create the actual person.
Its inverse is a static method `toBuilder(Person)`:

~~~ java
public class PersonBuilder extends GenericStruct<PersonBuilder> {
    public String name = null;
    public Integer age = null;
    public String profession = null;

    public Person build() {
        return new Person(name, age, profession);
    }
    
    public static PersonBuilder toBuilder(Person person) {
        PersonBuilder builder = new Builder();
        builder.name = person.getName();
        builder.age = person.getAge();
        builder.profession = person.getProfession();
        return builder;
    }
}
~~~

This is the final builder-object. The parser itself looks
as follows:

~~~ kotlin
val person = Utils.builder(PersonBuilder.class)
             .then(Utils.setter("name", id)).then(comma)
             .then(Utils.setter("age", num)).then(comma)
             .then(Utils.setter("profession", id))
             .then(Utils.build(PersonBuilder.class));
~~~ 

The advantage of this method is that the builder-objects
are really simple to implement, different types
can be mixed, inversion works out of the box and
all properties have meaningful names. The
disadvantage is the heavy use of reflection, thus
in some cases using a list or a map might be preferable.

