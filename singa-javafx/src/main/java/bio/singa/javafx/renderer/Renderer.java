package bio.singa.javafx.renderer;

import bio.singa.mathematics.geometry.edges.Line;
import bio.singa.mathematics.geometry.edges.LineSegment;
import bio.singa.mathematics.geometry.edges.Parabola;
import bio.singa.mathematics.geometry.edges.SimpleLineSegment;
import bio.singa.mathematics.geometry.faces.Circle;
import bio.singa.mathematics.geometry.faces.Rectangle;
import bio.singa.mathematics.geometry.faces.VertexPolygon;
import bio.singa.mathematics.geometry.model.Polygon;
import bio.singa.mathematics.vectors.Vector2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

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
     * <ul>
     * <li> The color is determined by the FillColor (set by {@link GraphicsContext#setFill(Paint)}).</li>
     * </ul>
     *
     * @param point The position of the point.
     * @param radius The diameter of the point.
     */
    default void fillPoint(Vector2D point, double radius) {
        getGraphicsContext().fillOval(
                point.getX() - radius,
                point.getY() - radius,
                radius * 2.0,
                radius * 2.0);
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
        fillPoint(point, getGraphicsContext().getLineWidth() * 2.0);
    }

    default void strokeCircle(Circle circle) {
        getGraphicsContext().strokeOval(
                circle.getMidpoint().getX() - circle.getRadius(),
                circle.getMidpoint().getY() - circle.getRadius(),
                circle.getRadius() * 2.0,
                circle.getRadius() * 2.0);
    }

    default void strokeCircle(Vector2D midpoint, double radius) {
        getGraphicsContext().strokeOval(
                midpoint.getX() - radius,
                midpoint.getY() - radius,
                radius * 2.0,
                radius * 2.0);
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
        getGraphicsContext().strokePolyline(
                vectors.stream().mapToDouble(Vector2D::getX).toArray(),
                vectors.stream().mapToDouble(Vector2D::getY).toArray(),
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
        getGraphicsContext().strokeLine(start.getX(), start.getY(), end.getX(), end.getY());
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
        getGraphicsContext().setLineDashes(dashes);
        strokeStraight(lineSegment.getStartingPoint(), lineSegment.getEndingPoint());
        getGraphicsContext().setLineDashes(null);
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
        final TextAlignment initialTextAlign = getGraphicsContext().getTextAlign();
        getGraphicsContext().setTextAlign(TextAlignment.CENTER);
        double height = new Text(text).getLayoutBounds().getHeight()/4.0;
        getGraphicsContext().fillText(text, center.getX(), center.getY()+height);
        getGraphicsContext().setTextAlign(initialTextAlign);
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
        getGraphicsContext().fillRect(topLeftCorner.getX(), topLeftCorner.getY(), rectangle.getHeight(), rectangle.getWidth());
    }

    default void strokeRectangle(Rectangle rectangle) {
        getGraphicsContext().strokeRect(rectangle.getTopLeftVertex().getX(), rectangle.getTopLeftVertex().getY(), rectangle.getHeight(), rectangle.getWidth());
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
        getGraphicsContext().fillRect(rectangle.getTopLeftVertex().getX(), rectangle.getTopLeftVertex().getY(), rectangle.getHeight(), rectangle.getWidth());
        return rectangle;
    }


    default void strokeLineSegmentWithArrow(LineSegment lineSegment) {
        Vector2D tail = lineSegment.getStartingPoint();
        Vector2D head = lineSegment.getEndingPoint();

        double arrowLength = 12;

        double tipX = head.getX();
        double tipY = tail.getY();

        double tailX = tail.getX();
        double tailY = tail.getY();

        double dx = tipX - tailX;
        double dy = tipY - tailY;

        double theta = Math.atan2(dy, dx);

        double rad = Math.toRadians(25); //35 angle, can be adjusted
        double x = tipX - arrowLength * Math.cos(theta + rad);
        double y = tipY - arrowLength * Math.sin(theta + rad);

        double phi2 = Math.toRadians(-25);//-35 angle, can be adjusted
        double x2 = tipX - arrowLength * Math.cos(theta + phi2);
        double y2 = tipY - arrowLength * Math.sin(theta + phi2);

        double[] arrowYs = new double[3];
        arrowYs[0] = tipY;
        arrowYs[1] = y;
        arrowYs[2] = y2;

        double[] arrowXs = new double[3];
        arrowXs[0] = tipX;
        arrowXs[1] = x;
        arrowXs[2] = x2;
        strokeStraight(tail, head);
        getGraphicsContext().fillPolygon(arrowXs, arrowYs, 3);
    }

    /**
     * Fills the polygon. <ul> <li> The color is determined by the FillColor (set by {@link
     * GraphicsContext#setFill(Paint)}).</li> </ul>
     *
     * @param polygon The polygon to draw.
     */
    default void fillPolygon(Polygon polygon) {
        int numberOfVertices = polygon.getNumberOfVertices();
        double[] xPositions = new double[numberOfVertices];
        double[] yPositions = new double[numberOfVertices];
        List<Vector2D> vertices = polygon.getVertices();
        for (int index = 0; index < vertices.size(); index++) {
            Vector2D vertex = vertices.get(index);
            xPositions[index] = vertex.getX();
            yPositions[index] = vertex.getY();
        }
        getGraphicsContext().fillPolygon(xPositions, yPositions, numberOfVertices);
    }

    default void fillPolygon(Vector2D... vertices) {
        fillPolygon(new VertexPolygon(vertices));
    }

    default void strokePolygon(Polygon polygon) {
        int numberOfVertices = polygon.getNumberOfVertices();
        double[] xPositions = new double[numberOfVertices];
        double[] yPositions = new double[numberOfVertices];
        List<Vector2D> vertices = polygon.getVertices();
        for (int index = 0; index < vertices.size(); index++) {
            Vector2D vertex = vertices.get(index);
            xPositions[index] = vertex.getX();
            yPositions[index] = vertex.getY();
        }
        getGraphicsContext().strokePolygon(xPositions, yPositions, numberOfVertices);
    }

}
