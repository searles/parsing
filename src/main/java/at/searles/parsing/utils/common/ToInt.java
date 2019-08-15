package at.searles.parsing.utils.common;

import at.searles.parsing.ParserCallBack;
import at.searles.parsing.Mapping;
import at.searles.parsing.ParserStream;
import at.searles.parsing.PrinterCallBack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ToInt implements Mapping<CharSequence, Integer> {
    @Override
    public Integer parse(ParserCallBack env, ParserStream stream, @NotNull CharSequence left) {
        try {
            return Integer.parseInt(left.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Nullable
    @Override
    public CharSequence left(PrinterCallBack env, @NotNull Integer result) {
        return result.toString();
    }
}
