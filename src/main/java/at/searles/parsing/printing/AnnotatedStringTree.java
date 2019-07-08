package at.searles.parsing.printing;

import java.util.function.BiFunction;

public class AnnotatedStringTree<C> implements StringTree {
    private final StringTree parent;
    private final C annotation;

    public AnnotatedStringTree(StringTree parent, C annotation) {
        this.parent = parent;
        this.annotation = annotation;
    }

    public String toString() {
        return parent.toString();
    }

    @Override
    public StringBuilder toStringBuilder(StringBuilder sb, BiFunction<Object, StringTree, StringTree> annotationInserts) {
        return annotationInserts.apply(annotation, parent).toStringBuilder(sb, annotationInserts);
    }
}
