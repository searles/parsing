package at.searles.parsing

import at.searles.parsing.ref.RefParser

class PossiblyInfiniteRecursionException(source: RefParser<*>, e: StackOverflowError?) : RuntimeException("Possibly infinite recursion in $source", e)