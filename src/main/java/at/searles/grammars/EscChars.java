package at.searles.grammars;

import at.searles.parsing.Environment;
import at.searles.parsing.Mapping;
import at.searles.parsing.ParserStream;

/**
 * Maps special characters.
 */
class EscChars implements Mapping<CharSequence, Integer> {
    @Override
    public Integer parse(Environment env, CharSequence left, ParserStream stream) {
        switch (left.charAt(1)) {
            case 'x':case 'u':case'U':
                return parseHex(left);
            case 'n':
                return (int) '\n';
            case 'r':
                return (int) '\r';
            case 't':
                return (int) '\t';
            case 'b':
                return (int) '\b';
            case 'f':
                return (int) '\f';
            default:
                return (int) left.charAt(1);
        }
    }

    private int parseHex(CharSequence seq) {
        // first char is simply characterization,
        // numbers afterwards are the hex number.
        int hex = 0;

        for (int i = 2; i < seq.length(); ++i) {
            hex *= 16;

            char ch = seq.charAt(i);

            if (ch <= '9') {
                hex += ch - '0';
            } else if (ch <= 'F') {
                hex += ch - 'A' + 10;
            } else {
                hex += ch - 'a' + 10;
            }
        }

        return hex;
    }
}
