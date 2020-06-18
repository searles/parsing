package at.searles.regexparser;

import at.searles.regexp.CharSet;
import at.searles.regexp.Regexp;

public class RegexpParser {

    public static Regexp parse(String string) {
        CodePointStream stream = new CodePointStream(string);
        Regexp regexp = union(stream);

        if (!stream.trim().end()) {
            throw new RegexpParserException("Not fully parsed: " + stream);
        }

        return regexp;
    }

    private static int integer(CodePointStream stream) {
        int num = stream.get() - '0';

        if(num < 0 || 9 < num) {
            throw new IllegalArgumentException();
        }

        for (int digit = stream.incr().get(); '0' <= digit && digit <= '9'; digit = stream.incr().get()) {
            num = num * 10 + digit - '0';
        }

        return num;
    }

    static Regexp union(CodePointStream stream) {
        Regexp regexp = concat(stream);

        if (regexp == null) {
            return null;
        }

        while (stream.trim().get() == '|') {
            stream.incr().trim();
            Regexp next = concat(stream);
            if (next == null) {
                throw new RegexpParserException("Could not parse content after |");
            }

            regexp = regexp.or(next);
        }

        return regexp;
    }

    private static Regexp concat(CodePointStream stream) {
        Regexp regexp = null;
        while(true) {
            Regexp next = qualified(stream.trim());
            if(next == null) return regexp;
            regexp = regexp != null ? regexp.then(next) : next;
        }
    }

    private static Regexp qualified(CodePointStream stream) {
        // may return null
        Regexp regexp = term(stream);
        if (regexp == null) {
            return null;
        }

        switch(stream.get()) {
            case '*': stream.incr(); return regexp.rep();
            case '+': stream.incr(); return regexp.plus();
            case '?': stream.incr(); return regexp.opt();
            case '!': stream.incr(); return regexp.nonGreedy();
            case '{': return range(regexp, stream);
            default: return regexp;
        }
    }

    /**
     * regex{min,max} or regex{count} or regex{min,}
     */
    private static Regexp range(Regexp regexp, CodePointStream stream) {
        if (stream.get() != '{') {
            return regexp;
        }

        stream.incr();

        int start = integer(stream);

        if(stream.get() == '}') {
            return regexp.count(start);
        }

        if(stream.get() != ',') {
            throw new RegexpParserException("missing ','");
        }

        stream.incr();

        if(stream.get() == '}') {
            return regexp.min(start);
        }

        int end = integer(stream);

        if(stream.get() != '}') {
            throw new RegexpParserException("missing '}'");
        }

        stream.incr();

        return regexp.range(start, end);
    }

    private static Regexp group(CodePointStream stream) {
        if (stream.get() != '(') {
            return null;
        }

        Regexp regexp = union(stream.incr());

        if (stream.get() != ')') {
            throw new RegexpParserException("missing ')'");
        }

        stream.incr();
        return regexp;
    }

    // the only thing that terminates a regex is a closing ).
    private static Regexp term(CodePointStream stream) {
        Regexp group = group(stream);

        if(group != null) {
            return group;
        }

        CharSet set = CharSetParser.charSet(stream);

        if(set != null) {
            return set;
        }

        String str = EscStringParser.parse(stream);

        if(str == null) {
            str = RawStringParser.fetch(stream);
        }

        if (str != null) {
            return Regexp.text(str);
        }

        return null;
    }
}
