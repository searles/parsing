package at.searles.parsing.utils.common;

import at.searles.parsing.Initializer;
import at.searles.parsing.ParserStream;

/**
 * Initializer that introduces a simple number
 */
public class Num implements Initializer<Integer> {

    private final int num;

    public Num(int num) {
        this.num = num;
    }

    @Override
    public Integer parse(ParserStream stream) {
        return num;
    }

    @Override
    public boolean consume(Integer integer) {
        return integer == num;
    }

    @Override
    public String toString() {
        return String.format("{num: %d}", num);
    }
}
