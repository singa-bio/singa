package de.bioforscher.singa.javafx.renderer;

import com.sun.javafx.tk.FontMetrics;
import com.sun.javafx.tk.Toolkit;
import de.bioforscher.singa.mathematics.geometry.edges.Line;
import de.bioforscher.singa.mathematics.geometry.edges.LineSegment;
import de.bioforscher.singa.mathematics.geometry.edges.Parabola;
import de.bioforscher.singa.mathematics.geometry.faces.Rectangle;
import de.bioforscher.singa.mathematics.geometry.model.Polygon;
import de.bioforscher.singa.mathematics.vectors.Vector2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Paint;

import java.util.*;

/**
 * The renderer interface supplies default methods to draw in a {@link GraphicsContext} method.
 */
public interface Renderer {

    /**
     * Returns the assigned GraphicContext.
     *
     * @return The assigned GraphicContext.
     */
    GraphicsContext getGraphicsContext();

    double getDrawingWidth();

    double getDrawingHeight();

    /**
     * Draws a point (filled circle) where the {@link Vector2D} is positioned. The point is centered on the vector.<br>
     * <ul> <li> The color is determined by the FillColor (set by {@link GraphicsContext#setFill(Paint)}).</li> </ul>
     *
     * @param point The position of the point.
     * @param diameter The diameter of the point.
     */
    default void drawPoint(Vector2D point, double diameter) {
        getGraphicsContext().fillOval(
                point.getX() - diameter / 2.0,
                point.getY() - diameter / 2.0,
                diameter,
                diameter);
    }

    /**
     * Draws a point (filled circle) where the {@link Vector2D} is positioned. The point is centered on the vector.<br>
     * <ul> <li> The diameter of the point is determined by the LineWidth (set by {@link
     * GraphicsContext#setLineWidth(double)}).</li> <li> The color is determined by the FillColor (set by {@link
     * GraphicsContext#setFill(Paint)}).</li> </ul>
     *
     * @param point The position of the point.
     */
    default void drawPoint(Vector2D point) {
        drawPoint(point, getGraphicsContext().getLineWidth());
    }

    /**
     * Circles the outline of a point where the {@link Vector2D} is positioned. The point is centered on the vector
     * .<br> <ul> <li> The color is determined by the FillColor (set by {@link GraphicsContext#setFill(Paint)}).</li>
     * </ul>
     *
     * @param point The position of the point.
     * @param diameter The diameter of the circle.
     */
    default void circlePoint(Vector2D point, double diameter) {
        getGraphicsContext().strokeOval(
                point.getX() - diameter / 2.0,
                point.getY() - diameter / 2.0,
                diameter,
                diameter);
    }

    /**
     * Connects the points given in the List in order of their appearance with a line.<br> <ul> <li> The line width of
     * the point is determined by the LineWidth (set by {@link GraphicsContext#setLineWidth(double)}).</li> <li> The
     * color is determined by the StrokeColor (set by {@link GraphicsContext#setStroke(Paint)}).</li> </ul>
     *
     * @param vectors The points to be connected with a line.
     */
    default void connectPoints(Collection<Vector2D> vectors) {
        getGraphicsContext().strokePolyline(
                vectors.stream().mapToDouble(Vector2D::getX).toArray(),
                vectors.stream().mapToDouble(Vector2D::getY).toArray(),
                vectors.size()
        );
    }

    /**
     * Draws a straight by connecting the given start and end points. <ul> <li> The line width of the point is
     * determined by the LineWidth (set by {@link GraphicsContext#setLineWidth(double)}).</li> <li> The color is
     * determined by the StrokeColor (set by {@link GraphicsContext#setStroke(Paint)}).</li> </ul>
     *
     * @param start The starting point.
     * @param end The ending point.
     */
    default void drawStraight(Vector2D start, Vector2D end) {
        getGraphicsContext().strokeLine(start.getX(), start.getY(), end.getX(), end.getY());
    }

    /**
     * Draws the given line segment. <ul> <li> The line width of the point is determined by the LineWidth (set by {@link
     * GraphicsContext#setLineWidth(double)}).</li> <li> The color is determined by the StrokeColor (set by {@link
     * GraphicsContext#setStroke(Paint)}).</li> </ul>
     *
     * @param lineSegment The line segment.
     */
    default void drawLineSegment(LineSegment lineSegment) {
        drawStraight(lineSegment.getStartingPoint(), lineSegment.getEndingPoint());
    }

    /**
     * Draws the given line segment. <ul> <li> The line width of the point is determined by the LineWidth (set by {@link
     * GraphicsContext#setLineWidth(double)}).</li> <li> The color is determined by the StrokeColor (set by {@link
     * GraphicsContext#setStroke(Paint)}).</li> </ul>
     *
     * @param dashes An array of finite non negative dash length.
     * @param lineSegment The line segment.
     */
    default void dashLineSegment(LineSegment lineSegment, double... dashes) {
        getGraphicsContext().setLineDashes(dashes);
        drawStraight(lineSegment.getStartingPoint(), lineSegment.getEndingPoint());
        getGraphicsContext().setLineDashes(null);
    }

    /**
     * Draws the given line. The line is drawn over the whole displayed Canvas. <ul> <li> The line width of the point is
     * determined by the LineWidth (set by {@link GraphicsContext#setLineWidth(double)}).</li> <li> The color is
     * determined by the StrokeColor (set by {@link GraphicsContext#setStroke(Paint)}).</li> </ul>
     *
     * @param line The line.
     */
    default void drawLine(Line line) {
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
            start = line.getInterceptWithLine(new Line(0, 0));
            end = line.getInterceptWithLine(new Line(maxY, 0));
        }
        drawStraight(start, end);
    }

    /**
     * Draws the given parabola. The parabola is drawn over the whole displayed Canvas. <ul> <li> The line width of the
     * point is determined by the LineWidth (set by {@link GraphicsContext#setLineWidth(double)}).</li> <li> The color
     * is determined by the StrokeColor (set by {@link GraphicsContext#setStroke(Paint)}).</li> </ul>
     *
     * @param parabola The parabola.
     */
    default void drawParabola(Parabola parabola) {
        drawParabola(parabola, 20);
    }

    /**
     * Draws the given parabola. The parabola is drawn over the whole displayed Canvas. The sampling depth gives the
     * number of points that are connected to draw the parabola. The higher the depth, the finer the parabola is drawn.
     * <ul> <li> The line width of the point is determined by the LineWidth (set by {@link
     * GraphicsContext#setLineWidth(double)}).</li> <li> The color is determined by the StrokeColor (set by {@link
     * GraphicsContext#setStroke(Paint)}).</li> </ul>
     *
     * @param parabola The parabola.
     * @param samplingDepth The number of points that are connected to draw the parabola.
     */
    default void drawParabola(Parabola parabola, int samplingDepth) {
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
     * Draws the given text centered on the given vector. <ul> <li> The font settings are determined by the graphics
     * context.</li> <li> The color is determined by the FillColor (set by {@link GraphicsContext#setFill(Paint)}).</li>
     * </ul>
     *
     * @param text The text to draw.
     * @param center The point to center onto.
     */
    default void drawTextCenteredOnPoint(String text, Vector2D center) {
        final FontMetrics fm = Toolkit.getToolkit().getFontLoader().getFontMetrics(getGraphicsContext().getFont());
        final double x = center.getX() - (fm.computeStringWidth(text) / 2);
        final double y = center.getY() - (fm.getLineHeight() / 2) + fm.getAscent();
        getGraphicsContext().fillText(text, x, y);
    }

    /**
     * Draws the given rectangle. <ul> <li> The color is determined by the FillColor (set by {@link
     * GraphicsContext#setFill(Paint)}).</li> </ul>
     *
     * @param topLeftCorner The top left corner of the rectangle;
     * @param bottomRightCorner The bottom right corner of the rectangle.
     */
    default void drawRectangle(Vector2D topLeftCorner, Vector2D bottomRightCorner) {
        Rectangle rectangle = new Rectangle(topLeftCorner, bottomRightCorner);
        getGraphicsContext().fillRect(topLeftCorner.getX(), topLeftCorner.getY(), rectangle.getHeight(), rectangle.getWidth());
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
    default Rectangle drawDraggedRectangle(Vector2D firstCorner, Vector2D secondCorner) {
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
        getGraphicsContext().fillRect(rectangle.getTopLeftVertex().getX(), rectangle.getTopLeftVertex().getY(), rectangle.getHeight(), rectangle.getWidth());
        return rectangle;
    }

    /**
     * Fills the polygon. <ul> <li> The color is determined by the FillColor (set by {@link
     * GraphicsContext#setFill(Paint)}).</li> </ul>
     * @param polygon The polygon to draw.
     */
    default void fillPolygon(Polygon polygon) {
        int numberOfVertices = polygon.getNumberOfVertices();
        double[] xPositions = new double[polygon.getNumberOfVertices()];
        double[] yPositions = new double[polygon.getNumberOfVertices()];
        Vector2D[] vertices = polygon.getVertices();
        for (int index = 0; index < vertices.length; index++) {
            Vector2D vertex = vertices[index];
            xPositions[index] = vertex.getX();
            yPositions[index] = vertex.getY();
        }
        getGraphicsContext().fillPolygon(xPositions, yPositions, numberOfVertices);
    }

}
