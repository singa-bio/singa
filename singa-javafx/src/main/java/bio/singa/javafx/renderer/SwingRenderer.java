package bio.singa.javafx.renderer;

import bio.singa.mathematics.geometry.edges.Line;
import bio.singa.mathematics.geometry.edges.LineSegment;
import bio.singa.mathematics.geometry.edges.Parabola;
import bio.singa.mathematics.geometry.edges.SimpleLineSegment;
import bio.singa.mathematics.geometry.faces.Circle;
import bio.singa.mathematics.geometry.faces.Rectangle;
import bio.singa.mathematics.geometry.model.Polygon;
import bio.singa.mathematics.vectors.Vector2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Paint;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * The renderer interface supplies default methods to draw in a {@link GraphicsContext} method.
 */
public interface SwingRenderer {

    /**
     * Returns the assigned GraphicContext.
     *
     * @return The assigned GraphicContext.
     */
    Graphics2D getGraphicsContext();

    double getDrawingWidth();

    double getDrawingHeight();

    /**
     * Draws a point (filled circle) where the {@link Vector2D} is positioned. The point is centered on the vector.<br>
     * <ul>
     * <li> The color is determined by the FillColor (set by {@link GraphicsContext#setFill(Paint)}).</li>
     * </ul>
     *
     * @param point The position of the point.
     * @param radius The diameter of the point.
     */
    default void fillPoint(Vector2D point, double radius) {
        getGraphicsContext().fillOval(
                ((int) (point.getX() - radius)),
                ((int) (point.getY() - radius)),
                ((int) (radius * 2.0)),
                ((int) (radius * 2.0)));
    }

    /**
     * Draws a point (filled circle) where the {@link Vector2D} is positioned. The point is centered on the vector.<br>
     * <ul>
     * <li> The diameter of the point is determined by the LineWidth (set by
     * {@link GraphicsContext#setLineWidth(double)}).</li>
     * <li> The color is determined by the FillColor (set by {@link GraphicsContext#setFill(Paint)}).</li>
     * </ul>
     *
     * @param point The position of the point.
     */
    default void fillPoint(Vector2D point) {
        fillPoint(point, ((BasicStroke) getGraphicsContext().getStroke()).getLineWidth() * 2.0);
    }

    default void strokeCircle(Circle circle) {
        getGraphicsContext().drawOval(
                ((int) (circle.getMidpoint().getX() - circle.getRadius())),
                ((int) (circle.getMidpoint().getY() - circle.getRadius())),
                ((int) (circle.getRadius() * 2.0)),
                ((int) (circle.getRadius() * 2.0)));
    }

    default void strokeCircle(Vector2D midpoint, double radius) {
        getGraphicsContext().drawOval(
                ((int) (midpoint.getX() - radius)),
                ((int) (midpoint.getY() - radius)),
                ((int) (radius * 2.0)),
                ((int) (radius * 2.0)));
    }

    default void fillCircle(Circle circle) {
        fillPoint(circle.getMidpoint(), circle.getRadius());
    }

    /**
     * Connects the points given in the List in order of their appearance with a line.<br>
     * <ul>
     * <li> The line width of the point is determined by the LineWidth (set by
     * {@link GraphicsContext#setLineWidth(double)}).</li>
     * <li> The color is determined by the StrokeColor (set by {@link GraphicsContext#setStroke(Paint)}).</li>
     * </ul>
     *
     * @param vectors The points to be connected with a line.
     */
    default void connectPoints(Collection<Vector2D> vectors) {
        getGraphicsContext().drawPolyline(
                vectors.stream().mapToInt(vector2D -> (int) vector2D.getX()).toArray(),
                vectors.stream().mapToInt(vector2D -> (int) vector2D.getY()).toArray(),
                vectors.size()
        );
    }

    /**
     * Draws a straight by connecting the given start and end points.
     * <ul>
     * <li> The line width of the point is determined by the LineWidth (set by
     * {@link GraphicsContext#setLineWidth(double)}).</li>
     * <li> The color is determined by the StrokeColor (set by {@link GraphicsContext#setStroke(Paint)}).</li>
     * </ul>
     *
     * @param start The starting point.
     * @param end The ending point.
     */
    default void strokeStraight(Vector2D start, Vector2D end) {
        getGraphicsContext().drawLine(((int) start.getX()), ((int) start.getY()), ((int) end.getX()), ((int) end.getY()));
    }

    /**
     * Draws the given line segment.
     * <ul>
     * <li> The line width of the point is determined by the LineWidth (set by
     * {@link GraphicsContext#setLineWidth(double)}).</li>
     * <li> The color is determined by the StrokeColor (set by {@link GraphicsContext#setStroke(Paint)}).</li>
     * </ul>
     *
     * @param lineSegment The line segment.
     */
    default void strokeLineSegment(LineSegment lineSegment) {
        strokeStraight(lineSegment.getStartingPoint(), lineSegment.getEndingPoint());
    }

    /**
     * Draws the given line segment.
     * <ul>
     * <li> The line width of the point is determined by the LineWidth (set by
     * {@link GraphicsContext#setLineWidth(double)}).</li>
     * <li> The color is determined by the StrokeColor (set by {@link GraphicsContext#setStroke(Paint)}).</li>
     * </ul>
     *
     * @param dashes An array of finite non negative dash length.
     * @param lineSegment The line segment.
     */
    default void dashLineSegment(SimpleLineSegment lineSegment, double... dashes) {
        throw new UnsupportedOperationException("Unsupported for swing rendering");
    }

    /**
     * Draws the given line. The line is drawn over the whole displayed Canvas.
     * <ul>
     * <li> The line width of the point is determined by the LineWidth (set by
     * {@link GraphicsContext#setLineWidth(double)}).</li>
     * <li> The color is determined by the StrokeColor (set by {@link GraphicsContext#setStroke(Paint)}).</li>
     * </ul>
     *
     * @param line The line.
     */
    default void strokeLine(Line line) {
        final double minX = 0;
        final double maxX = getDrawingWidth();
        final double minY = 0;
        final double maxY = getDrawingHeight();

        Vector2D start;
        Vector2D end;
        if (line.isHorizontal()) {
            start = new Vector2D(minX, line.getYIntercept());
            end = new Vector2D(maxX, line.getYIntercept());
        } else if (line.isVertical()) {
            start = new Vector2D(line.getXIntercept(), minY);
            end = new Vector2D(line.getXIntercept(), maxY);
        } else {
            start = line.getIntersectWithLine(new Line(0, 0));
            end = line.getIntersectWithLine(new Line(maxY, 0));
        }
        strokeStraight(start, end);
    }

    /**
     * Draws the given parabola. The parabola is drawn over the whole displayed Canvas.
     * <ul>
     * <li> The line width of the point is determined by the LineWidth (set by
     * {@link GraphicsContext#setLineWidth(double)}).</li>
     * <li> The color is determined by the StrokeColor (set by {@link GraphicsContext#setStroke(Paint)}).</li>
     * </ul>
     *
     * @param parabola The parabola.
     */
    default void strokeParabola(Parabola parabola) {
        strokeParabola(parabola, 20);
    }

    /**
     * Draws the given parabola. The parabola is drawn over the whole displayed Canvas. The sampling depth gives the
     * number of points that are connected to draw the parabola. The higher the depth, the finer the parabola is drawn.
     * <ul>
     * <li> The line width of the point is determined by the LineWidth (set by
     * {@link GraphicsContext#setLineWidth(double)}).</li>
     * <li> The color is determined by the StrokeColor (set by {@link GraphicsContext#setStroke(Paint)}).</li>
     * </ul>
     *
     * @param parabola The parabola.
     * @param samplingDepth The number of points that are connected to draw the parabola.
     */
    default void strokeParabola(Parabola parabola, int samplingDepth) {
        final double minX = 0;
        final double maxX = getDrawingWidth();
        final double maxY = getDrawingHeight();

        List<Vector2D> list = new ArrayList<>();

        Vector2D leftMost;
        Vector2D rightMost;
        if (!parabola.isOpenTowardsXAxis()) {
            // calculate intercepts with horizontal line with the y intercept at maximal displayable y value
            SortedSet<Vector2D> xIntercepts = parabola.getIntercepts(new Line(maxY, 0));
            if (xIntercepts.first().getX() < minX) {
                leftMost = new Vector2D(minX, parabola.getYValue(minX));
            } else {
                leftMost = xIntercepts.first();
            }
            list.add(leftMost);
            if (xIntercepts.last().getX() > maxX) {
                rightMost = new Vector2D(maxX, parabola.getYValue(maxX));
            } else {
                rightMost = xIntercepts.last();
            }
            list.add(rightMost);
        } else {
            // calculate intercepts with x axis
            SortedSet<Double> xIntercepts = parabola.getXIntercepts();
            if (xIntercepts.first() < minX) {
                leftMost = new Vector2D(minX, parabola.getYValue(minX));
            } else {
                leftMost = new Vector2D(xIntercepts.first(), 0);
            }
            list.add(leftMost);
            if (xIntercepts.last() > maxX) {
                rightMost = new Vector2D(maxX, parabola.getYValue(maxX));
            } else {
                rightMost = new Vector2D(xIntercepts.last(), 0);
            }
            list.add(rightMost);
        }

        final double maximalExtend = leftMost.distanceTo(rightMost);
        final double offset = maximalExtend / samplingDepth;

        for (double currentX = leftMost.getX() + offset; currentX < rightMost.getX(); currentX += offset) {
            list.add(new Vector2D(currentX, parabola.getYValue(currentX)));
        }

        list.sort(Comparator.comparing(Vector2D::getX));
        connectPoints(list);
    }

    /**
     * Draws the given text centered on the given vector.
     * <ul>
     * <li> The font settings are determined by the graphics context.</li>
     * <li> The color is determined by the FillColor (set by {@link GraphicsContext#setFill(Paint)}).</li>
     * </ul>
     *
     * @param text The text to draw.
     * @param center The point to center onto.
     */
    default void strokeTextCenteredOnPoint(String text, Vector2D center) {
        // Get the FontMetrics
        FontMetrics metrics = getGraphicsContext().getFontMetrics(getGraphicsContext().getFont());
        // Determine the X coordinate for the text
        int x = (int) (center.getX() - metrics.stringWidth(text) / 2.0);
        // Determine the Y coordinate for the text (note we add the ascent, as in java 2d 0 is top of the screen)
        int y = (int) (center.getY() - metrics.getHeight() / 2.0 + metrics.getAscent());
        // Draw the String
        getGraphicsContext().drawString(text, x, y);
    }

    /**
     * Draws the given rectangle. <ul> <li> The color is determined by the FillColor (set by {@link
     * GraphicsContext#setFill(Paint)}).</li> </ul>
     *
     * @param topLeftCorner The top left corner of the rectangle;
     * @param bottomRightCorner The bottom right corner of the rectangle.
     */
    default void fillRectangle(Vector2D topLeftCorner, Vector2D bottomRightCorner) {
        Rectangle rectangle = new Rectangle(topLeftCorner, bottomRightCorner);
        getGraphicsContext().fillRect(((int) topLeftCorner.getX()), ((int) topLeftCorner.getY()), ((int) rectangle.getHeight()), ((int) rectangle.getWidth()));
    }

    default void strokeRectangle(Rectangle rectangle) {
        getGraphicsContext().drawRect(((int) rectangle.getTopLeftVertex().getX()), ((int) rectangle.getTopLeftVertex().getY()), ((int) rectangle.getHeight()), ((int) rectangle.getWidth()));
    }

    /**
     * Draws a dragged rectangle evaluating and rearranging the corners, such that there is always a valid rectangle
     * that can be filled. <ul> <li> The color is determined by the FillColor (set by {@link
     * GraphicsContext#setFill(Paint)}).</li> </ul>
     *
     * @param firstCorner The first corner.
     * @param secondCorner The second (dragged) corner.
     * @return The rectangle that was drawn.
     */
    default Rectangle fillDraggedRectangle(Vector2D firstCorner, Vector2D secondCorner) {
        Rectangle rectangle;
        if (firstCorner.isLeftOf(secondCorner) && firstCorner.isAbove(secondCorner)) {
            rectangle = new Rectangle(firstCorner, secondCorner);
        } else if (firstCorner.isLeftOf(secondCorner) && firstCorner.isBelow(secondCorner)) {
            rectangle = new Rectangle(new Vector2D(firstCorner.getX(), secondCorner.getY()), new Vector2D(secondCorner.getX(), firstCorner.getY()));
        } else if (firstCorner.isRightOf(secondCorner) && firstCorner.isAbove(secondCorner)) {
            rectangle = new Rectangle(new Vector2D(secondCorner.getX(), firstCorner.getY()), new Vector2D(firstCorner.getX(), secondCorner.getY()));
        } else {
            rectangle = new Rectangle(secondCorner, firstCorner);
        }
        getGraphicsContext().fillRect(((int) rectangle.getTopLeftVertex().getX()), ((int) rectangle.getTopLeftVertex().getY()), ((int) rectangle.getHeight()), ((int) rectangle.getWidth()));
        return rectangle;
    }

    /**
     * Fills the polygon. <ul> <li> The color is determined by the FillColor (set by {@link
     * GraphicsContext#setFill(Paint)}).</li> </ul>
     *
     * @param polygon The polygon to draw.
     */
    default void fillPolygon(Polygon polygon) {
        int numberOfVertices = polygon.getNumberOfVertices();
        int[] xPositions = new int[numberOfVertices];
        int[] yPositions = new int[numberOfVertices];
        List<Vector2D> vertices = polygon.getVertices();
        for (int index = 0; index < vertices.size(); index++) {
            Vector2D vertex = vertices.get(index);
            xPositions[index] = (int) vertex.getX();
            yPositions[index] = (int) vertex.getY();
        }
        getGraphicsContext().fillPolygon(xPositions, yPositions, numberOfVertices);
    }

    default void strokePolygon(Polygon polygon) {
        int numberOfVertices = polygon.getNumberOfVertices();
        int[] xPositions = new int[numberOfVertices];
        int[] yPositions = new int[numberOfVertices];
        List<Vector2D> vertices = polygon.getVertices();
        for (int index = 0; index < vertices.size(); index++) {
            Vector2D vertex = vertices.get(index);
            xPositions[index] = (int) vertex.getX();
            yPositions[index] = (int) vertex.getY();
        }
        getGraphicsContext().drawPolygon(xPositions, yPositions, numberOfVertices);
    }

    default Color toAWTColor(javafx.scene.paint.Color fxColor) {
        return new java.awt.Color((float) fxColor.getRed(),
                (float) fxColor.getGreen(),
                (float) fxColor.getBlue(),
                (float) fxColor.getOpacity());
    }


}
