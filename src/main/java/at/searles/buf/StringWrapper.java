package at.searles.buf;

import org.jetbrains.annotations.NotNull;

public class StringWrapper implements FrameStream {

    private final String str;
    private final Frame frame; // singleton
    private int ptr;
    private int frameStart;
    private int frameEnd;

    public StringWrapper(String str) {
        this.str = str;
        this.ptr = 0;
        this.frameStart = this.frameEnd = 0;
        this.frame = new CharSeq();
    }

    @Override
    public int next() {
        if (ptr >= str.length()) {
            return -1;
        }

        int ch = str.codePointAt(ptr);
        ptr += Character.charCount(ch);
        return ch;
    }

    @Override
    public long position() {
        return ptr;
    }

    @Override
    public void setPositionTo(long ptr) {
        this.frameStart = this.frameEnd = this.ptr = (int) ptr;
    }

    @Override
    public void markFrameEnd() {
        assert this.ptr >= this.frameStart;
        this.frameEnd = this.ptr;
    }

    @Override
    public void advanceFrame() {
        this.ptr = this.frameStart = this.frameEnd;
    }

    @Override
    public void resetFrame() {
        this.ptr = this.frameEnd = this.frameStart;
    }

    @Override
    public Frame frame() {
        return frame;
    }

    @Override
    public String toString() {
        return str.substring(Math.max(0, frameStart - 16), frameStart)
                + "_"
                + str.substring(frameStart, frameEnd)
                + "_"
                + str.substring(frameEnd, Math.min(str.length(), frameEnd + 16));
    }

    private class CharSeq implements Frame {
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
            return frameEnd - frameStart;
        }

        @Override
        public char charAt(int index) {
            return str.charAt(index + frameStart);
        }

        @Override
        public CharSequence subSequence(int start, int end) {
            return str.substring(frameStart + start, frameStart + end);
        }

        @NotNull
        @Override
        public String toString() {
            return str.substring(frameStart, frameEnd);
        }
    }
}
