package at.searles.parsing.combinators;

import at.searles.parsing.*;
import at.searles.parsing.printing.PartialStringTree;
import at.searles.parsing.printing.StringTree;

public class ReducerThenRecognizer<T, U> implements Reducer<T, U>, Recognizable.Then {
    private final Reducer<T, U> left;
    private final Recognizer right;

    public ReducerThenRecognizer(Reducer<T, U> left, Recognizer right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public Recognizable left() {
        return left;
    }

    @Override
    public Recognizable right() {
        return right;
    }

    @Override
    public U parse(Environment env, T left, ParserStream stream) {
        long offset = stream.offset();

        long preStart = stream.start();
        long preEnd = stream.end();

        U result = this.left.parse(env, left, stream);

        assert stream.start() == preStart;

        if(result == null) {
            return null;
        }

        if(!right.recognize(env, stream)) {
            env.notifyNoMatch(stream, this, right);

            stream.setOffset(offset);
            assert stream.start() == preStart;
            stream.setEnd(preEnd);

            return null;
        }

        stream.setStart(preStart);

        return result;
    }


    @Override
    public boolean recognize(Environment env, ParserStream stream) {
        long preStart = stream.start();

        boolean status = Recognizable.Then.super.recognize(env, stream);

        if(status) {
            stream.setStart(preStart);
        }

        return status;
    }

    @Override
    public PartialStringTree<T> print(Environment env, U u) {
        PartialStringTree<T> leftOutput = left.print(env, u);

        if(leftOutput == null) {
            return null;
        }

        StringTree rightOutput = right.print(env);

        return new PartialStringTree<>(leftOutput.left, leftOutput.right.consRight(rightOutput));
    }

    @Override
    public String toString() {
        return createString();
    }
}
