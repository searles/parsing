package at.searles.regexparser;

public class RawStringParser {
    /**
     * single-quoted raw string
     */
    public static String fetch(CodePointStream stream) {
        if (stream.get() != '\'') {
            return null;
        }

        stream.incr();

        StringBuilder sb = new StringBuilder();

        while (true) {
            int cp = stream.get();

            if(cp == -1) {
                throw new RegexParserException("unexpected end: " + stream);
            }

            if(cp == '\'') {
                stream.incr();
                return sb.toString();
            }

            sb.appendCodePoint(chr(stream));
        }
    }

    static int chr(CodePointStream stream) {
        int cp = stream.get();

        if(cp != '\\') {
            stream.incr();
            return cp;
        }

        cp = stream.incr().get();

        if(cp == '\'' || cp == '\\') {
            stream.incr();
            return cp;
        }

        return '\\';
    }

    public static String unparse(String input) {
        StringBuilder sb = new StringBuilder("'");
        for(int i = 0; i < input.length(); ++i) {
            if(input.charAt(i) == '\\') {
                sb.append("\\\\");
            } else if(input.charAt(i) == '\'') {
                sb.append("\\'");
            } else {
                sb.append(input.charAt(i));
            }
        }

        return sb.append("'").toString();
    }
}
