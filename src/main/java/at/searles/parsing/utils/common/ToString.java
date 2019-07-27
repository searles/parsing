package at.searles.parsing.utils.common;

import at.searles.parsing.Environment;
import at.searles.parsing.Mapping;
import at.searles.parsing.ParserStream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ToString implements Mapping<CharSequence, String> {

    private static class Holder {
        static final ToString INSTANCE = new ToString();
    }

    public static ToString getInstance() {
        return Holder.INSTANCE;
    }

    @Override
    public String parse(Environment env, @NotNull CharSequence left, ParserStream stream) {
        return left.toString();
    }

    @Nullable
    @Override
    public CharSequence left(Environment env, @NotNull String result) {
        return result;
    }
}
