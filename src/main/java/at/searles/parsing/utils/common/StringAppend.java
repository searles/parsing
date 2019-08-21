package at.searles.parsing.utils.common;

import at.searles.parsing.Fold;
import at.searles.parsing.ParserStream;
import org.jetbrains.annotations.NotNull;

/**
 * Append codePoint to String.
 */
public class StringAppend implements Fold<String, Integer, String> {
    @Override
    public String apply(ParserStream stream, @NotNull String left, @NotNull Integer right) {
        return left + new String(Character.toChars(right));
    }

    @Override
    public String leftInverse(@NotNull String result) {
        if (result.isEmpty()) {
            return null;
        }

        if (Character.isHighSurrogate(result.charAt(result.length() - 1))) {
            return result.substring(0, result.length() - 2);
        } else {
            return result.substring(0, result.length() - 1);
        }
    }

    @Override
    public Integer rightInverse(@NotNull String result) {
        if (result.isEmpty()) {
            return null;
        }

        return result.codePointAt(result.length() - 1);
    }

    @Override
    public String toString() {
        return "{string + codepoint}";
    }
}
