package at.searles.buf;

import org.jetbrains.annotations.NotNull;

public class FrameStreamImpl implements FrameStream {

    private final BufferedStream.Impl stream;
    private final CharSeq frame;
    private long frameStart;
    private long frameEnd;
    private boolean invalid;

    public FrameStreamImpl(Impl stream) {
        this.stream = stream;
        this.invalid = false;
        this.frame = new CharSeq();
    }

    @Override
    public int next() {
        // is the frame too big?
        if (frameEnd - frameStart >= stream.bufSize()) {
            throw new IllegalArgumentException("buffer size is too small");
        }

        return stream.next();
    }

    @Override
    public long position() {
        return stream.position();
    }

    @Override
    public void setPositionTo(long ptr) {
        invalid = true;
        stream.setPositionTo(ptr);
        this.frameStart = ptr;
        this.frameEnd = ptr;
    }

    @Override
    public void mark() {
        invalid = true;
        this.frameEnd = stream.position();
    }

    @Override
    public void advance() {
        invalid = true;
        this.frameStart = this.frameEnd;
        stream.setPositionTo(this.frameEnd);
    }

    @Override
    public void reset() {
        invalid = true;
        this.frameEnd = this.frameStart;
        stream.setPositionTo(this.frameStart);
    }

    @Override
    public Frame frame() {
        return frame;
    }

    private class CharSeq implements Frame {
        private String str;

        private void update() {
            if (invalid) {
                StringBuilder sb = new StringBuilder();

                for (long index = frameStart; index < frameEnd; ++index) {
                    sb.appendCodePoint(stream.cached(index));
                }

                str = sb.toString();
                invalid = false;
            }
        }

        @Override
        public long startPosition() {
            return frameStart;
        }

        @Override
        public long endPosition() {
            return frameEnd;
        }

        @Override
        public int length() {
            update();
            return str.length();
        }

        @Override
        public char charAt(int index) {
            update();
            return str.charAt(index);
        }

        @Override
        public CharSequence subSequence(int start, int end) {
            update();
            return str.subSequence(start, end);
        }

        @NotNull
        public String toString() {
            update();
            return str;
        }
    }
}
