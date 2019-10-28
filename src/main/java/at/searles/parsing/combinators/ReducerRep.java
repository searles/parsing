package at.searles.parsing.combinators;

import at.searles.parsing.*;
import at.searles.parsing.printing.ConcreteSyntaxTree;
import at.searles.parsing.printing.PartialConcreteSyntaxTree;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Parser for repetitions
 */
public class ReducerRep<T> implements Reducer<T, T>, Recognizable.Rep {
    private final Reducer<T, T> parent;

    public ReducerRep(Reducer<T, T> parent) {
        this.parent = parent;
    }

    @Override
    public T parse(ParserStream stream, @NotNull T left) {
        long preStart = stream.getStart();

        while (true) {
            T t = parent.parse(stream, left);

            // Contract of Reducer
            assert stream.getStart() == preStart;

            if (t == null) break;

            left = t;
        }

        return left;
    }


    @Override
    public Recognizable parent() {
        return parent;
    }

    @Override
    public PartialConcreteSyntaxTree<T> print(@NotNull T t) {
        T left = t;

        ArrayList<ConcreteSyntaxTree> trees = new ArrayList<>();

        for (; ; ) {
            PartialConcreteSyntaxTree<T> next = parent.print(left);

            if (next == null) {
                break;
            }

            trees.add(next.right);

            left = next.left;
        }

        Collections.reverse(trees);

        return new PartialConcreteSyntaxTree<>(left, ConcreteSyntaxTree.fromList(trees));
    }

    @Override
    public String toString() {
        return createString();
    }
}
