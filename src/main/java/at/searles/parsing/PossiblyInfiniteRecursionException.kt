package at.searles.parsing

import at.searles.parsing.ref.Ref

class PossiblyInfiniteRecursionException(source: Ref<*>, e: StackOverflowError?) : RuntimeException("Possibly infinite recursion in $source", e)