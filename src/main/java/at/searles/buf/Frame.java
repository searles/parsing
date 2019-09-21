package at.searles.buf;

public interface Frame extends CharSequence {
    /**
     * The pointer to the current frame start.
     */
    long startPosition();

    /**
     * The pointer to the current frame start.
     */
    long endPosition();
}
