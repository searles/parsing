package at.searles.parsing.utils.common;

import at.searles.parsing.Environment;
import at.searles.parsing.Initializer;
import at.searles.parsing.ParserStream;

/**
 * Created by searles on 01.04.19.
 */
public class Num implements Initializer<Integer> {

    private final int num;

    public Num(int num) {
        this.num = num;
    }

    @Override
    public Integer parse(Environment env, ParserStream stream) {
        return num;
    }

    @Override
    public boolean consume(Environment env, Integer integer) {
        return integer == num;
    }

    @Override
    public String toString() {
        return String.format("{num: %d}", num);
    }
}
