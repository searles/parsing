package at.searles.parsing;

import at.searles.parsing.printing.PartialConcreteSyntaxTree;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ReducerRef<T, U> implements Reducer<T, U> {
    private final Reducer<T, U> parent;
    private final String label;

    public ReducerRef(Reducer<T, U> parent, String label) {
        this.parent = parent;
        this.label = label;
    }

    @Nullable
    @Override
    public U parse(ParserStream stream, @NotNull T left) {
        return parent.parse(stream, left);
    }

    @Nullable
    @Override
    public PartialConcreteSyntaxTree<T> print(@NotNull U u) {
        return parent.print(u);
    }

    @Override
    public boolean recognize(ParserStream stream) {
        return parent.recognize(stream);
    }

    @Override
    public String toString() {
        return label;
    }
}
