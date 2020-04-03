package at.searles.parsing;

import at.searles.parsing.printing.ConcreteSyntaxTree;
import at.searles.parsing.printing.PartialConcreteSyntaxTree;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RecognizerRef implements Recognizer {
    private final Recognizer parent;
    private final String label;

    public RecognizerRef(Recognizer parent, String label) {
        this.parent = parent;
        this.label = label;
    }

    @Nullable
    @Override
    public boolean recognize(ParserStream stream) {
        return parent.recognize(stream);
    }

    @NotNull
    @Override
    public ConcreteSyntaxTree print() {
        return parent.print();
    }

    @Override
    public String toString() {
        return label;
    }
}
