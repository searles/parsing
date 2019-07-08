package at.searles.parsing;

/**
 * Everything that can be recognized
 */
public interface Recognizable {

    boolean recognize(Environment env, ParserStream stream);

    interface Then extends Recognizable {
        Recognizable left();
        Recognizable right();

        @Override
        default boolean recognize(Environment env, ParserStream stream) {
            long offset = stream.offset();

            long preStart = stream.start();
            long preEnd = stream.end();

            if (!left().recognize(env, stream)) {
                return false;
            }

            long start = stream.start();

            if (!right().recognize(env, stream)) {
                env.notifyNoMatch(stream, this);
                stream.setOffset(offset);
                stream.setStart(preStart);
                stream.setEnd(preEnd);
                return false;
            }

            stream.setStart(start);
            return true;
        }

        default String createString() {
            String leftString = left().toString();
            String rightString = right().toString();

            if(left() instanceof Or) {
                leftString = "(" + leftString + ")";
            }

            if(right() instanceof Or || right() instanceof Then) {
                rightString = "(" + rightString + ")";
            }

            return leftString + " " + rightString;
        }
    }

    interface Or extends Recognizable {
        Recognizable first();
        Recognizable second();

        @Override
        default boolean recognize(Environment env, ParserStream stream) {
            return first().recognize(env, stream) || second().recognize(env, stream);
        }

        default String createString() {
            String firstString = first().toString();
            String secondString = second().toString();

            if(second() instanceof Or) {
                secondString = "(" + secondString + ")";
            }

            return firstString + " | " + secondString;
        }
    }

    interface Opt extends Recognizable {
        Recognizable parent();

        @Override
        default boolean recognize(Environment env, ParserStream stream) {
            stream.setStart(stream.end());
            parent().recognize(env, stream);

            return true;
        }

        default String createString() {
            String parentString = parent().toString();

            if(parent() instanceof Or || parent() instanceof Then) {
                parentString = "(" + parentString + ")";
            }

            return parentString + "?";
        }
    }

    interface Rep extends Recognizable {
        Recognizable parent();

        @Override
        default boolean recognize(Environment env, ParserStream stream) {
            long start = stream.start();

            while(parent().recognize(env, stream)) {
                // do nothing. Everything is done in 'recognize'
                stream.setStart(start);
            }

            return true;
        }

        default String createString() {
            String parentString = parent().toString();

            if(parent() instanceof Or || parent() instanceof Then) {
                parentString = "(" + parentString + ")";
            }

            return parentString + "*";
        }
    }
}
