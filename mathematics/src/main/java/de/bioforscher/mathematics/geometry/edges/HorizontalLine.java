package de.bioforscher.mathematics.geometry.edges;

/**
 * A horizontal line is a line parallel to the x-axis of the coordinate plane.
 *
 * @author Christoph Leberecht
 * @version 2.0.0
 */
public class HorizontalLine extends Line {

    /**
     * Creates a new horizontal line at a specific height.
     *
     * @param y The height or y-value of the line.
     */
    public HorizontalLine(double y) {
        super(y, 0);
    }

    /**
     * Gets the height or y-value of the line.
     *
     * @return The height or y-value of the line.
     */
    public double getPosition() {
        return getYIntercept();
    }

    /**
     * Moves the horizontal line to a specified position.
     *
     * @param y The new height or y-value of the line.
     */
    public HorizontalLine moveTo(double y) {
        return new HorizontalLine(y);
    }

    /**
     * Moves the horizontal line down by the specified amount.
     *
     * @param delta The amount to move.
     */
    public HorizontalLine moveDown(double delta) {
        return moveTo(getYIntercept() - delta);
    }

    /**
     * Moves the horizontal line up by the specified amount.
     *
     * @param delta The amount to move.
     */
    public HorizontalLine moveUp(double delta) {
        return moveTo(getYIntercept() + delta);
    }

}
