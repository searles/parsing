# Perspectives

## Code Formatter

For a code formatter, the following strategies might be useful

Only `recognize` should be called.

Input: The string
Output: Formatting rules like `replace positions 0-1 by "\n  "`.


* Call-back function in Lexer `tokenCallBack(FrameStream stream, IntSet tokenIds)` where
the frame stream can be used to obtain the exact position. 
** Can be done by extending `Lexer` and overriding `nextToken`.
** Also needed to collect eg comments.
* Call-back for annotations, one for beginning, one for end.
** `annotationCallBack(ParserStream stream, Annotation a)
** Drawback: If parent-parser in AnnotationParser fails, then how to recover?

### Example run

#### Setting

Very simple grammar:

~~~
expr: term+ ;
term: '(' expr ')' | 'a' ;
~~~

Formatting should indent one space for each `(`, all arguments should be separated
by one space. `a(aa(a)aa)` should be formatted as

~~~
a (
 a a (
  a
 ) a a
)
~~~

The attributed grammar (yes, I guess this is a bit of an abuse of terminology) is

~~~
expr: term (term >> `app`)* ;
term: '(' (expr @ `BLOCK`) ')' | 'a' >> `id` ;
~~~

The fold `app` creates a `BinNode`, `id` is a Mapping to an `IdNode` where 
`BinNode` and `IdNode` are instances of `AstNode` of our Ast.

#### Conclusions from Run

* Remove all hidden tokens (white chars in this case). Otherwise, initial indentation eg in `  a` would not be treated correctly.
** Consequence: Formatting rules are `delete range [start-end]` and `insert at N`.

#### Run

~~~
Input: ' a'
Output: 'delete 0-1' since hidden token.

Input: 'aa'
Parser: term[a] (term[a] >> app @ ARGUMENT)
Rule: insert one space before ARGUMENT.
Output: 'insert 1 " "' 
~~~

So far, easy. But if an annotation has some other effect, eg increment
indentation, either start/end/fail of an annotation parser must be tracked,
or all positions must be stored. The latter seems impractical. 

Suggestion: In AnnotationParser (simiuse the following:

~~~ java
    @Override
    public T parse(ParserStream stream) {
        stream.notifyAnnotationStart(annotation);
        T ret = parser.parse(stream);
        stream.notifyAnnotationEnd(annotation, /*successful:*/ ret != null);
        return ret;
    }
~~~

~~~
Input: '(a)'
Parser: term[a] @ BLOCK
Rule: at block-start, insert new line, and increment indentation level.
~~~

## Syntax highlighter

Just on token-level

## Only 2 seconds after keystroke

~~~
DELAY = 2000; // ms

long lastChange;
Runnable nextCall;

onKeyPressed() {
    // in UI Thread
    lastChange = System.getTimeStamp();
    
    if(nextCall == null) {
        nextCall = () -> callMe();
        Timer.scheduleOnce(DELAY, nextCall);
    }
}

callMe() {
    // also in UI-Thread.
    long expectedCallDelay = lastChange + DELAY - System.getTimeStamp();
    
    if(expectedCallDelay > 0) {
        // don't run now.
        Timer.scheduleOnce(expectedCallDelay, nextCall);
        return;
    }

    nextCall = null;

    // do stuff
}
~~~

## Environment

Logically, the following call would make much more sense:

~~~
parse(ParserStream stream)
~~~

The LL(1)-check should be rather hard-coded into the parser. Same for
the ll-printing-thing.

On the other hand, three Environments - ParserCallBack, TokenCallBack, PrinterCallBack - make
most sense. Also nice for keeping symbol tables.
