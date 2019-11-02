package at.searles.regexparser;

public class EscStringParser {
    /**
     * double-quoted. Allows \\Uxxxxxxxx, \\uxxxx, \\xXX and other common escape sequences like \\r
     */
    public static String fetch(CodePointStream stream) {
        if (stream.get() != '\"') {
            return null;
        }

        stream.incr();

        StringBuilder sb = new StringBuilder();

        while (true) {
            int cp = stream.get();

            if(cp == -1) {
                throw new IllegalArgumentException(); // FIXME
            }

            if(cp == '\"') {
                stream.incr();
                return sb.toString();
            }

            sb.appendCodePoint(EscCharParser.chr(stream));
        }
    }

    public static String unparse(String input) {
        CodePointStream stream = new CodePointStream(input);
        StringBuilder sb = new StringBuilder("\"");

        while(!stream.end()) {
            appendCodePoint(stream.get(), sb);
            stream.incr();
        }

        return sb.append('"').toString();
    }

    public static String toJavaString(String input) {
        CodePointStream stream = new CodePointStream(input);
        StringBuilder sb = new StringBuilder("\"");

        while(!stream.end()) {
            appendJavaCodePoint(stream.get(), sb);
            stream.incr();
        }

        return sb.append('"').toString();
    }

    private static void appendCodePoint(int cp, StringBuilder sb) {
        if(cp == 0) {
            sb.append("\\0");
        } else if(cp == '\n') {
            sb.append("\\n");
        } else if(cp == '\r') {
            sb.append("\\r");
        } else if(cp == '\t') {
            sb.append("\\t");
        } else if(cp == '\b') {
            sb.append("\\b");
        } else if(cp < ' ') {
            sb.append(String.format("\\x%02x", cp));
        } else if(cp < 0x7f) {
            sb.appendCodePoint(cp);
        } else if(cp < 0xff) {
            sb.append(String.format("\\x%02x", cp));
        } else if(cp < 0xffff) {
            sb.append(String.format("\\u%04x", cp));
        } else {
            sb.append(String.format("\\U%08x", cp));
        }
    }

    private static void appendJavaCodePoint(int cp, StringBuilder sb) {
        if(cp == 0) {
            sb.append("\\0");
        } else if(cp == '\n') {
            sb.append("\\n");
        } else if(cp == '\r') {
            sb.append("\\r");
        } else if(cp == '\t') {
            sb.append("\\t");
        } else if(cp == '\b') {
            sb.append("\\b");
        } else if(cp < ' ') {
            sb.append(String.format("\\u%04x", cp));
        } else if(cp < 0x7f) {
            sb.appendCodePoint(cp);
        } else {
            char[] chrs = Character.toChars(cp);

            for(char ch : chrs) {
                sb.append(String.format("\\u%04x", (int) ch));
            }
        }
    }
}
