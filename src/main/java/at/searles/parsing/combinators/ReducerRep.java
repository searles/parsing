package at.searles.parsing.combinators;

import at.searles.parsing.*;
import at.searles.parsing.printing.PartialConcreteSyntaxTree;
import at.searles.parsing.printing.ConcreteSyntaxTree;
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
    public T parse(Environment env, @NotNull T left, ParserStream stream) {
        long preStart = stream.start();

        while(true) {
            T t = parent.parse(env, left, stream);

            // Contract of Reducer
            assert stream.start() == preStart;

            if(t == null) break;

            left = t;
        }

        return left;
    }


    @Override
    public Recognizable parent() {
        return parent;
    }

    @Override
    public PartialConcreteSyntaxTree<T> print(Environment env, @NotNull T t) {
        T left = t;

        ArrayList<ConcreteSyntaxTree> trees = new ArrayList<>();

        for(;;) {
            PartialConcreteSyntaxTree<T> next = parent.print(env, left);

            if(next == null) {
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
