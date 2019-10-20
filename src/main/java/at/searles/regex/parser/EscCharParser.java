package at.searles.regex.parser;

public class EscCharParser {

    private EscCharParser() {}

    /**
     * Returns the next (possibly escaped) character
     */
    public static int chr(CodePointStream stream) {
        int cp = stream.get();
        stream.incr();

        if(cp != '\\') {
            return cp;
        }

        cp = stream.get();
        stream.incr();

        switch(cp) {
            case 'U': return hex(8, stream);
            case 'u': return hex(4, stream);
            case 'x': return hex(2, stream);
            case 'n': return '\n';
            case 'r': return '\r';
            case 't': return '\t';
            case 'b': return '\b';
            case '0': return 0;
            default: return cp;
        }
    }

    private static int hexDigit(int digit) {
        if ('0' <= digit && digit <= '9') {
            return digit - '0';
        } else if ('A' <= digit && digit <= 'F') {
            return digit - 'A' + 10;
        } else if ('a' <= digit && digit <= 'f') {
            return digit - 'a' + 10;
        } else {
            throw new IllegalArgumentException(); // FIXME
        }
    }

    private static int hex(int digitCount, CodePointStream stream) {
        int ret = hexDigit(stream.get());
        for (int i = 1; i < digitCount; ++i) {
            ret *= 16 + hexDigit(stream.incr().get());
        }
        stream.incr();
        return ret;
    }
}
