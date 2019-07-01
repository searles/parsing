package at.searles.utils;

public class Conversions {
    /**
     * Converts the string in seq into a quoted string. For this,
     * backslashes and quotes replaced.
     * @param seq The sequence
     * @return "seq"
     */
    public static String quote(CharSequence seq) {
        StringBuilder sb = new StringBuilder();

        sb.append('"');

        for(int i = 0; i < seq.length(); ++i) {
            char ch = seq.charAt(i);
            switch(ch) {
                case '"':
                    sb.append("\\\"");
                    break;
                default:
                    sb.append(ch);
                    break;
            }
        }

        sb.append('"');

        return sb.toString();
    }

    /**
     * Inverse of quote
     */
    public static String unquote(CharSequence seq) {
        StringBuilder sb = new StringBuilder();

        if(seq.charAt(0) != '"' || seq.charAt(seq.length() - 1) != '"') {
            throw new IllegalArgumentException("not a quoted string");
        }

        for(int i = 1; i < seq.length() - 1; ++i) {
            char ch = seq.charAt(i);
            if(ch == '\\') {
                i++;
            }

            sb.append(ch);
        }

        return sb.toString();
    }
}
