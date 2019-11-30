package at.searles.parsing;

/**
 * Everything that can be recognized
 */
public interface Recognizable {

    boolean recognize(ParserStream stream);

    interface Then extends Recognizable {
        Recognizable left();

        Recognizable right();

        @Override
        default boolean recognize(ParserStream stream) {
            long offset = stream.getOffset();

            long preStart = stream.getStart();
            long preEnd = stream.getEnd();

            if (!left().recognize(stream)) {
                return false;
            }

            long start = stream.getStart();

            if (!right().recognize(stream)) {
                if(stream.getOffset() != offset) {
                    throwIfNoBacktrack(stream);
                    stream.backtrackToOffset(offset);
                }

                stream.setStart(preStart);
                stream.setEnd(preEnd);
                return false;
            }

            stream.setStart(start);
            return true;
        }

        default void throwIfNoBacktrack(ParserStream stream) {
            if(!allowParserBacktrack()) {
                throw new ParserLookaheadException(this, stream);
            }
        }

        boolean allowParserBacktrack();

        default String createString() {
            String leftString = left().toString();
            String rightString = right().toString();

            if (left() instanceof Or) {
                leftString = "(" + leftString + ")";
            }

            if (right() instanceof Or || right() instanceof Then) {
                rightString = "(" + rightString + ")";
            }

            return leftString + " " + rightString;
        }
    }

    interface Or extends Recognizable {
        Recognizable first();

        Recognizable second();

        @Override
        default boolean recognize(ParserStream stream) {
            return first().recognize(stream) || second().recognize(stream);
        }

        default String createString() {
            String firstString = first().toString();
            String secondString = second().toString();

            if (second() instanceof Or) {
                secondString = "(" + secondString + ")";
            }

            return firstString + " | " + secondString;
        }
    }

    interface Opt extends Recognizable {
        Recognizable parent();

        @Override
        default boolean recognize(ParserStream stream) {
            stream.setStart(stream.getEnd());
            parent().recognize(stream);

            return true;
        }

        default String createString() {
            String parentString = parent().toString();

            if (parent() instanceof Or || parent() instanceof Then) {
                parentString = "(" + parentString + ")";
            }

            return parentString + "?";
        }
    }

    interface Rep extends Recognizable {
        Recognizable parent();

        @Override
        default boolean recognize(ParserStream stream) {
            long start = stream.getStart();

            while (parent().recognize(stream)) {
                // do nothing. Everything is done in 'recognize'
                stream.setStart(start);
            }

            return true;
        }

        default String createString() {
            String parentString = parent().toString();

            if (parent() instanceof Or || parent() instanceof Then) {
                parentString = "(" + parentString + ")";
            }

            return parentString + "*";
        }
    }
}
