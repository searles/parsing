package at.searles.regexparser;

import at.searles.lexer.utils.Interval;
import at.searles.regexp.CharSet;

public class CharSetParser {
    // quoted string may contain charsets and .
    public static CharSet charSet(CodePointStream stream) {
        int cp = stream.get();

        if (cp == '.') {
            stream.incr();
            return CharSet.Companion.all();
        }

        if (cp != '[') {
            return null;
        }

        boolean invert = false;

        if (stream.incr().get() == '^') {
            invert = true;
            stream.incr();
        }

        CharSet set = CharSet.Companion.empty();

        while (true) {
            cp = stream.get();

            if (cp == -1) {
                throw new RegexpParserException("unexpected end: " + stream);
            }

            if (cp == ']') {
                stream.incr();
                return invert ? set.invert() : set;
            }

            Interval interval = interval(stream);
            set = set.or(CharSet.Companion.interval(interval.getStart(), interval.getEnd() - 1));
        }
    }

    private static Interval interval(CodePointStream stream) {
        int start = EscCharParser.chr(stream);

        if(stream.get() == '-') {
            stream.incr();
            int end = EscCharParser.chr(stream);
            return new Interval(start, end + 1);
        }

        return new Interval(start, start + 1);
    }
}
