package at.searles.parsing.utils.common;

import at.searles.parsing.Environment;
import at.searles.parsing.Fold;
import at.searles.parsing.ParserStream;

/**
 * Append codePoint to String.
 */
public class StringAppend implements Fold<String, Integer, String> {
    @Override
    public String apply(Environment env, String left, Integer right, ParserStream stream) {
        return left + new String(Character.toChars(right));
    }

    @Override
    public String leftInverse(Environment env, String result) {
        if(result.isEmpty()) {
            return null;
        }

        if(Character.isHighSurrogate(result.charAt(result.length() - 1))) {
            // TODO check
            return result.substring(0, result.length() - 2);
        } else {
            return result.substring(0, result.length() - 1);
        }
    }

    @Override
    public Integer rightInverse(Environment env, String result) {
        if(result.isEmpty()) {
            return null;
        }

        return result.codePointAt(result.length() - 1);
    }

    @Override
    public String toString() {
        return "{string + codepoint}";
    }
}
