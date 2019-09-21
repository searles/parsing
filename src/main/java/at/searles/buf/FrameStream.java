package at.searles.buf;

/**
 * This stream allows to iterate through a char stream using
 * frames. These frames are char sequences. The setPositionTo method
 * in BufferedStream must reset the frame to the new position.
 */
public interface FrameStream extends BufferedStream {

    /**
     * sets the end mark to the current position position.
     */
    void mark();

    /**
     * sets the start of the frame to the end of it
     * (and also the underlying position). The stream
     * is then in a reset position
     */
    void advance();

    /**
     * Sets the frame end and the position back to the start of the
     * current frame.
     */
    void reset();

    /**
     * @return The char sequence that represents the current frame.
     * Only the toString() method is guaranteed to be implemented efficiently.
     */
    Frame frame();

}
