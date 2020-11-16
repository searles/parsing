package at.searles.parsing.printing

interface ConcreteSyntaxTree {
    /**
     * Print this syntax tree using the provided printer.
     *
     * @param visitor The non-null printer.
     */
    fun accept(visitor: CstVisitor)

    fun consRight(right: ConcreteSyntaxTree): ConcreteSyntaxTree {
        return ConsTree(this, right)
    }

    fun consLeft(left: ConcreteSyntaxTree): ConcreteSyntaxTree {
        return ConsTree(left, this)
    }

    companion object {
        private val EMPTY: ConcreteSyntaxTree = EmptyTree()

        fun empty(): ConcreteSyntaxTree {
            return EMPTY
        }

        fun fromCharSequence(seq: CharSequence): ConcreteSyntaxTree {
            return TokenTree(seq)
        }

        fun fromList(list: List<ConcreteSyntaxTree>): ConcreteSyntaxTree {
            return when {
                list.isEmpty() -> empty()
                list.size == 1 -> list[0]
                list.size == 2 -> list[0].consRight(list[1])
                else -> ListTree(list)
            }
        }
    }
}