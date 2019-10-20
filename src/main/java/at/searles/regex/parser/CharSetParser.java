package at.searles.regex.parser;

import at.searles.lexer.utils.Interval;
import at.searles.regex.CharSet;

public class CharSetParser {
    // quoted string may contain charsets and .
    public static CharSet charSet(CodePointStream stream) {
        int cp = stream.get();

        if (cp == '.') {
            stream.incr();
            return CharSet.all();
        }

        if (cp != '[') {
            return null;
        }

        boolean invert = false;

        if (stream.incr().get() == '^') {
            invert = true;
            stream.incr();
        }

        CharSet set = CharSet.empty();

        while (true) {
            cp = stream.get();

            if (cp == -1) {
                throw new IllegalArgumentException(); // FIXME
            }

            if (cp == ']') {
                stream.incr();
                return invert ? set.invert() : set;
            }

            Interval interval = interval(stream);
            set = set.union(CharSet.interval(interval.start, interval.end));
        }
    }

    private static Interval interval(CodePointStream stream) {
        int start = EscCharParser.chr(stream);

        if(stream.get() == '-') {
            stream.incr();
            int end = EscCharParser.chr(stream);
            return new Interval(start, end);
        }

        return new Interval(start, start);
    }
}
