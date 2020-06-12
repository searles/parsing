package at.searles.parsing

class PossiblyInfiniteRecursionException(source: Ref<*>, e: StackOverflowError?) : RuntimeException("Possibly infinite recursion in $source", e)