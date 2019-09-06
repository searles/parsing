package at.searles.parsing.annotation;

import at.searles.parsing.ParserStream;
import at.searles.parsing.Reducer;
import at.searles.parsing.printing.PartialConcreteSyntaxTree;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AnnotationReducer<C, U, T> implements Reducer<T, U> {
    private final C annotation;
    private final Reducer<T, U> reducer;

    public AnnotationReducer(C annotation, Reducer<T, U> reducer) {
        this.annotation = annotation;
        this.reducer = reducer;
    }

    @Nullable
    @Override
    public U parse(ParserStream stream, @NotNull T left) {
        stream.notifyAnnotationBegin(annotation);
        U value = reducer.parse(stream, left);
        stream.notifyAnnotationEnd(annotation, value != null);
        return value;
    }

    @Nullable
    @Override
    public PartialConcreteSyntaxTree<T> print(@NotNull U u) {
        PartialConcreteSyntaxTree<T> tree = reducer.print(u);

        if(tree == null) {
            return null;
        }

        return new PartialConcreteSyntaxTree<>(tree.left, tree.right.annotate(annotation));
    }

    @Override
    public boolean recognize(ParserStream stream) {
        stream.notifyAnnotationBegin(annotation);
        boolean status = reducer.recognize(stream);
        stream.notifyAnnotationEnd(annotation, status);
        return status;
    }
}
