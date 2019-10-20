package at.searles.regex;

@Deprecated
public class RegexParser {

    public static final int NOT_FULLY_PARSED = 1;
    public static final int UNEXPECTED_END = 2;
    public static final int MISSING_CLOSING = 3;
    public static final int NO_HEX_DIGIT = 4;
    public static final int NO_DIGIT = 5;
    public static final int MISSING_AFTER_CHOICE = 6;
    public static final int MISSING_CLOSING_RANGE = 7;

    public static Regex parse(String string) {
        ParserInstance parser = new ParserInstance(string);
        Regex regex = parser.choice();

        if (parser.hasNext()) {
            parser.error(NOT_FULLY_PARSED);
        }

        return regex;
    }

    public static class ParserException extends RuntimeException {
        public ParserException(String string, int position, int code) {
            super(String.format("Could not parse regex %s at %s, error code: %d", string, position, code));
        }
    }

    private static class ParserInstance {
        final String string;
        int i = 0;

        ParserInstance(String string) {
            this.string = string;
        }

        void error(int errorCode) {
            throw new ParserException(string, i, errorCode);
        }

        char ch() {
            // XXX if whitechars should not matter they can be skipped here.
            if (!hasNext()) {
                error(UNEXPECTED_END);
            }

            return string.charAt(i);
        }

        boolean hasNext() {
            return i < string.length();
        }

        void next() {
            ++i;
        }

        // # Single character, possibly escaped.

        char chr() {
            char ch = ch();
            next();
            return ch == '\\' ? escaped() : ch;
        }

        char escaped() {
            char ch = ch();
            next();
            switch (ch) {
                case 'u':
                    return hex(4);
                case 'x':
                    return hex(2);
                case 'n':
                    return '\n';
                case 'r':
                    return '\r';
                case 't':
                    return '\t';
                default:
                    return ch;
            }
        }

        char hex(int digits) {
            char ch = 0;

            for (int i = 0; i < digits; ++i) {
                ch *= 16;

                char digit = ch();

                if ('0' <= digit && digit <= '9') {
                    ch += digit - '0';
                } else if ('A' <= digit && digit <= 'F') {
                    ch += digit - 'A' + 10;
                } else if ('a' <= digit && digit <= 'f') {
                    ch += digit - 'a' + 10;
                } else {
                    error(NO_HEX_DIGIT);
                }

                next();
            }

            return ch;
        }

        int integer() {
            int num = 0;

            int length = 0;

            for (char digit = ch(); '0' <= digit && digit <= '9'; digit = ch()) {
                num = num * 10 + digit - '0';
                length++;
                next();
            }

            if (length == 0) {
                error(NO_DIGIT);
            }

            return num;
        }

        // single quoted string, returns null if not single-quoted.
        String singleQuoted() {
            if (ch() != '\'') {
                return null;
            }

            next(); // consume initializing '

            StringBuilder sb = new StringBuilder();

            while (ch() != '\'') {
                sb.append(chr());
            }

            next(); // consume terminating '

            return sb.toString();
        }

        // quoted string may contain charsets and .

        CharSet charSet() {
            if (ch() == '.') {
                next();
                return CharSet.all();
            }

            if (ch() != '[') {
                return null;
            }

            next();

            boolean invert = false;

            if (ch() == '^') {
                invert = true;
                next();
            }

            CharSet set = CharSet.empty();

            do {
                // there must be at least one element
                char start = chr();

                if (ch() == '-') {
                    next();
                    char end = ch();
                    set = set.union(CharSet.interval(start, end));
                } else {
                    set = set.union(CharSet.chars(start));
                }
            } while (ch() != ']');

            next(); // consume closing ]

            if (invert) {
                set = set.invert();
            }

            return set;
        }

        Regex then(Regex left, Regex right) {
            return left == null ? right : left.then(right);
        }

        // Quoted may contain charsets in between.
        Regex quoted() {
            if (ch() != '\"') {
                return null;
            }

            next(); // consume initializing "

            Regex regex = null;

            StringBuilder sb = new StringBuilder();

            while (ch() != '\"') {
                CharSet set = charSet();

                if (set != null) {
                    if (sb.length() != 0) {
                        regex = then(regex, Regex.text(sb.toString()));
                        sb.setLength(0);
                    }

                    regex = then(regex, set);
                } else {
                    sb.append(CharSet.chars(chr()));
                }

                regex = then(regex, set);
            }

            next(); // consume terminating "

            if (sb.length() != 0) {
                regex = then(regex, Regex.text(sb.toString()));
            }


            return regex;
        }

        // the only thing that terminates a regex is a closing ).
        Regex term() {
            if (!hasNext() || ch() == ')' || ch() == '|') {
                return null;
            }

            if (ch() == '(') {
                next();
                Regex regex = choice();

                if (ch() != ')') {
                    error(MISSING_CLOSING);
                } else {
                    next();
                }

                return regex;
            }

            CharSet set = charSet();

            if (set != null) {
                return set;
            }

            String str = singleQuoted();

            if (str != null) {
                return Regex.text(str);
            }

            char chr = chr();

            return Regex.text(Character.toString(chr));
        }

        /**
         * regex{min,max} or regex{count} or
         * regex{min,} or regex{,max}
         *
         */
        Regex repeat(Regex regex) {
            if (ch() != '{') {
                return null;
            }

            next();

            if (ch() == ',') {
                next();
                int max = integer();

                regex = regex.range(0, max);
            } else {
                int min = integer();

                if (ch() == ',') {
                    next();

                    if (ch() != '}') {
                        int max = integer();

                        regex = regex.range(min, max);
                    } else {
                        regex = regex.min(min);
                    }
                } else {
                    regex = regex.count(min);
                }
            }

            if (ch() != '}') {
                error(MISSING_CLOSING_RANGE);
            }

            next();

            return regex;
        }

        Regex qualified() {
            // may return null
            Regex regex = term();

            if (regex == null) {
                return null;
            }

            if (hasNext()) {
                if (ch() == '*') {
                    next();
                    regex = regex.rep();
                } else if (ch() == '+') {
                    next();
                    regex = regex.plus();
                } else if (ch() == '?') {
                    next();
                    regex = regex.opt();
                } else if (ch() == '!') {
                    // XXX Special one. Remove this for standard parsers.
                    next();
                    regex = regex.nonGreedy();
                } else {
                    Regex repeat = repeat(regex);

                    if (repeat != null) {
                        regex = repeat;
                    }
                }
            }

            return regex;
        }

        Regex concat() {
            // never returns null
            Regex regex = null;

            for (Regex next = qualified(); next != null; next = qualified()) {
                regex = then(regex, next);
            }

            return regex;
        }

        Regex choice() {
            Regex regex = concat();

            if (regex == null) {
                return null;
            }

            while (hasNext() && ch() == '|') {
                next();

                Regex next = concat();

                if (next == null) {
                    error(MISSING_AFTER_CHOICE);
                }

                regex = regex.or(next);
            }

            return regex;
        }
    }
}
