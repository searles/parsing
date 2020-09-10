package at.searles.parsing.printing

interface ConcreteSyntaxTree {
    /**
     * Print this syntax tree using the provided printer.
     *
     * @param printer The non-null printer.
     */
    fun printTo(printer: CstPrinter)
    fun consRight(right: ConcreteSyntaxTree): ConcreteSyntaxTree {
        return ConsConcreteSyntaxTree(this, right)
    }

    fun consLeft(left: ConcreteSyntaxTree): ConcreteSyntaxTree {
        return ConsConcreteSyntaxTree(left, this)
    }

    fun annotate(label: String): ConcreteSyntaxTree {
        return LabelledConcreteSyntaxTree(label, this)
    }

    companion object {
        private val EMPTY: ConcreteSyntaxTree = EmptyConcreteSyntaxTree()

        fun empty(): ConcreteSyntaxTree {
            return EMPTY
        }

        fun fromCharSequence(seq: CharSequence): ConcreteSyntaxTree {
            return LeafConcreteSyntaxTree(seq)
        }

        fun fromList(list: List<ConcreteSyntaxTree>): ConcreteSyntaxTree {
            return when {
                list.isEmpty() -> empty()
                list.size == 1 -> list[0]
                list.size == 2 -> list[0].consRight(list[1])
                else -> ListConcreteSyntaxTree(list)
            }
        }
    }
}