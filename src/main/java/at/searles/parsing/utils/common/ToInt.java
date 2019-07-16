package at.searles.parsing.utils.common;

import at.searles.parsing.Environment;
import at.searles.parsing.Mapping;
import at.searles.parsing.ParserStream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ToInt implements Mapping<CharSequence, Integer> {
    @Override
    public Integer parse(Environment env, @NotNull CharSequence left, ParserStream stream) {
        try {
            return Integer.parseInt(left.toString());
        } catch(NumberFormatException e) {
            return null;
        }
    }

    @Nullable
    @Override
    public CharSequence left(Environment env, @NotNull Integer result) {
        return result.toString();
    }
}
