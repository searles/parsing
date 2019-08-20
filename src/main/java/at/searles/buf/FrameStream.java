package at.searles.buf;

/**
 * This stream allows to iterate through a char stream using
 * frames. These frames are char sequences. The setPositionTo method
 * in BufferedStream must reset the frame to the new position.
 */
public interface FrameStream extends BufferedStream {

    /**
     * sets the end to the current position position.
     */
    void markFrameEnd();

    /**
     * sets the start of the frame to the end of it
     * (and also the underlying position)
     */
    void advanceFrame();

    /**
     * Sets the frame end and the position back to the start of the
     * current frame.
     */
    void resetFrame();

    /**
     * @return The char sequence that represents the current frame.
     * Only the toString() method is guaranteed to be implemented efficiently.
     */
    Frame frame();

    interface Frame extends CharSequence {
        /**
         * The pointer to the current frame start.
         */
        long startPosition();

        /**
         * The pointer to the current frame start.
         */
        long endPosition();
    }
}
