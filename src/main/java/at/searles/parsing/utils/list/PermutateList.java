package at.searles.parsing.utils.list;

import at.searles.parsing.ParserCallBack;
import at.searles.parsing.Mapping;
import at.searles.parsing.ParserStream;
import at.searles.parsing.PrinterCallBack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class PermutateList<T> implements Mapping<List<T>, List<T>> {
    private final int[] order;

    public PermutateList(int...order) {
        this.order = order;
    }

    @Override
    public List<T> parse(ParserCallBack env, ParserStream stream, @NotNull List<T> left) {
        ArrayList<T> list = new ArrayList<>(left.size());

        for(int index: order) {
            list.add(left.get(index));
        }

        return list;
    }

    @Nullable
    @Override
    public List<T> left(PrinterCallBack env, @NotNull List<T> result) {
        if(result.size() != order.length) {
            return null;
        }

        ArrayList<T> list = new ArrayList<>(result.size());

        for(int i = 0; i < order.length; ++i) {
            list.add(null);
        }

        for(int i = 0; i < order.length; ++i) {
            list.set(order[i], result.get(i));
        }

        return list;
    }
}
