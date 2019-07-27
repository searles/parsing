package at.searles.buf;

import org.jetbrains.annotations.NotNull;

public class FrameStreamImpl implements FrameStream {

    private final Impl stream;
    private long frameStart;
    private long frameEnd;

    private boolean invalid;
    private final CharSeq frame;

    public FrameStreamImpl(Impl stream) {
        this.stream = stream;
        this.invalid = false;
        this.frame = new CharSeq();
    }

    @Override
    public int next() {
        // if frame becomes too big, increase buffer size
        if(frameEnd - frameStart >= stream.bufSize()) {
            throw new IllegalArgumentException("buffer size is too small");
        }

        return stream.next();
    }

    @Override
    public long ptr() {
        return stream.ptr();
    }

    @Override
    public void setPtr(long ptr) {
        invalid = true;
        stream.setPtr(ptr);
        this.frameStart = ptr;
        this.frameEnd = ptr;
    }

    @Override
    public void markFrameEnd() {
        invalid = true;
        this.frameEnd = stream.ptr();
    }

    @Override
    public void flushFrame() {
        invalid = true;
        this.frameStart = this.frameEnd;
        stream.setPtr(this.frameEnd);
    }

    @Override
    public void resetFrame() {
        invalid = true;
        this.frameEnd = this.frameStart;
        stream.setPtr(this.frameStart);
    }

    @Override
    public long frameStart() {
        return frameStart;
    }

    @Override
    public long frameEnd() {
        return frameEnd;
    }

    @Override
    public CharSequence frame() {
        return frame;
    }

    public class CharSeq implements CharSequence {
        private String str;

        private void update() {
            if(invalid) {
                StringBuilder sb = new StringBuilder();

                for(long index = frameStart; index < frameEnd; ++index) {
                    sb.appendCodePoint(stream.cached(index));
                }

                str = sb.toString();
                invalid = false;
            }
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
