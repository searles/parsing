package at.searles.parsing.utils.common;

import at.searles.parsing.Mapping;
import at.searles.parsing.ParserStream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ToString implements Mapping<CharSequence, String> {

    public static ToString getInstance() {
        return Holder.INSTANCE;
    }

    @Override
    public String parse(ParserStream stream, @NotNull CharSequence left) {
        return left.toString();
    }

    @Nullable
    @Override
    public CharSequence left(@NotNull String result) {
        return result;
    }

    private static class Holder {
        static final ToString INSTANCE = new ToString();
    }
}
