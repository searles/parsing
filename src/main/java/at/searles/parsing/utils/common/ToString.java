package at.searles.parsing.utils.common;

import at.searles.parsing.ParserCallBack;
import at.searles.parsing.Mapping;
import at.searles.parsing.ParserStream;
import at.searles.parsing.PrinterCallBack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ToString implements Mapping<CharSequence, String> {

    public static ToString getInstance() {
        return Holder.INSTANCE;
    }

    @Override
    public String parse(ParserCallBack env, ParserStream stream, @NotNull CharSequence left) {
        return left.toString();
    }

    @Nullable
    @Override
    public CharSequence left(PrinterCallBack env, @NotNull String result) {
        return result;
    }

    private static class Holder {
        static final ToString INSTANCE = new ToString();
    }
}
