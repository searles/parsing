package at.searles.regex.parser;

import at.searles.regex.CharSet;
import at.searles.regex.Regex;

public class StringToRegex {

    public static Regex parse(String string) {
        CodePointStream stream = new CodePointStream(string);
        Regex regex = union(stream);

        if (!stream.trim().end()) {
            throw new IllegalArgumentException(); // FIXME
        }

        return regex;
    }

    static int integer(CodePointStream stream) {
        int num = stream.get() - '0';

        if(num < 0 || 9 < num) {
            throw new IllegalArgumentException();
        }

        for (int digit = stream.incr().get(); '0' <= digit && digit <= '9'; digit = stream.incr().get()) {
            num = num * 10 + digit - '0';
        }

        return num;
    }

    static Regex union(CodePointStream stream) {
        Regex regex = concat(stream);

        if (regex == null) {
            return null;
        }

        while (stream.get() == '|') {
            stream.incr();
            Regex next = concat(stream);
            if (next == null) {
                throw new IllegalArgumentException(); // TODO
            }

            regex = regex.or(next);
        }

        return regex;
    }

    static Regex concat(CodePointStream stream) {
        Regex regex = null;
        while(true) {
            Regex next = qualified(stream);
            if(next == null) return regex;
            regex = regex != null ? regex.then(next) : next;
        }
    }

    static Regex qualified(CodePointStream stream) {
        // may return null
        Regex regex = term(stream);
        if (regex == null) {
            return null;
        }

        switch(stream.get()) {
            case '*': stream.incr(); return regex.rep();
            case '+': stream.incr(); return regex.plus();
            case '?': stream.incr(); return regex.opt();
            case '!': stream.incr(); return regex.nonGreedy();
            case '{': return range(regex, stream);
            default: return regex;
        }
    }

    /**
     * regex{min,max} or regex{count} or regex{min,}
     */
    private static Regex range(Regex regex, CodePointStream stream) {
        if (stream.get() != '{') {
            return regex;
        }

        stream.incr();

        int start = integer(stream);

        if(stream.get() == '}') {
            return regex.count(start);
        }

        if(stream.get() != ',') {
            throw new IllegalArgumentException(); // FIXME
        }

        stream.incr();

        if(stream.get() == '}') {
            return regex.min(start);
        }

        int end = integer(stream);

        if(stream.get() != '}') {
            throw new IllegalArgumentException(); // FIXME
        }

        stream.incr();

        return regex.range(start, end);
    }

    static Regex group(CodePointStream stream) {
        if (stream.get() != '(') {
            return null;
        }

        Regex regex = union(stream.incr());

        if (stream.get() != ')') {
            throw new IllegalArgumentException(); // FIXME
        }

        stream.incr();
        return regex;
    }

    // the only thing that terminates a regex is a closing ).
    static Regex term(CodePointStream stream) {
        Regex group = group(stream);

        if(group != null) {
            return group;
        }

        CharSet set = CharSetParser.charSet(stream);

        if(set != null) {
            return set;
        }

        String str = EscStringParser.fetch(stream);

        if(str == null) {
            str = RawStringParser.fetch(stream);
        }

        if (str != null) {
            return Regex.text(str);
        }

        return null;
    }
}
