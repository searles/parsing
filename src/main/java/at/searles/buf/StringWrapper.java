package at.searles.buf;

import org.jetbrains.annotations.NotNull;

public class StringWrapper implements FrameStream {

    private final CharSequence charSequence;
    private final Frame frame; // singleton
    private int ptr;
    private int frameStart;
    private int frameEnd;

    public StringWrapper(CharSequence charSequence) {
        this.charSequence = charSequence;
        this.ptr = 0;
        this.frameStart = this.frameEnd = 0;
        this.frame = new CharSeq();
    }

    private int codePointAt(int index) {
        char c1 = charSequence.charAt(index);

        if (Character.isHighSurrogate(c1) && ++index < charSequence.length()) {
            char c2 = charSequence.charAt(index);
            if (Character.isLowSurrogate(c2)) {
                return Character.toCodePoint(c1, c2);
            }
        }
        return c1;
    }

    @Override
    public int next() {
        if (ptr >= charSequence.length()) {
            return -1;
        }

        int ch = codePointAt(ptr);
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
    public void mark() {
        assert this.ptr >= this.frameStart;
        this.frameEnd = this.ptr;
    }

    @Override
    public void advance() {
        this.ptr = this.frameStart = this.frameEnd;
    }

    @Override
    public void reset() {
        this.ptr = this.frameEnd = this.frameStart;
    }

    @Override
    public Frame frame() {
        return frame;
    }

    @Override
    public String toString() {
        return charSequence.subSequence(Math.max(0, frameStart - 16), frameStart)
                + "_"
                + charSequence.subSequence(frameStart, frameEnd)
                + "_"
                + charSequence.subSequence(frameEnd, Math.min(charSequence.length(), frameEnd + 16));
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
            return charSequence.charAt(index + frameStart);
        }

        @Override
        public CharSequence subSequence(int start, int end) {
            return charSequence.subSequence(frameStart + start, frameStart + end);
        }

        @NotNull
        @Override
        public String toString() {
            return charSequence.subSequence(frameStart, frameEnd).toString();
        }
    }
}
