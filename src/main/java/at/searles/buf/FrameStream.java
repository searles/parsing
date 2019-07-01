package at.searles.buf;

/**
 * This stream allows to iterate through a char stream using
 * frames. These frames are char sequences. The setPtr method
 * in BufferedStream must reset the frame to the new position.
 */
public interface FrameStream extends BufferedStream {

    /**
     * sets the end to the current ptr position.
     */
    void markFrameEnd();

    /**
     * sets the start of the frame to the end of it
     * (and also the underlying ptr)
     */
    void flushFrame();

    /**
     * Sets the frame end and the ptr to the start.
     */
    void resetFrame();

    /**
     * The pointer to the current frame start.
     */
    long frameStart();

    /**
     * The pointer to the current frame start.
     */
    long frameEnd();

    /**
     * @return The char sequence that represents the current frame.
     * Only the toString() method is guaranteed to be implemented efficiently.
     */
    CharSequence frame();

}
