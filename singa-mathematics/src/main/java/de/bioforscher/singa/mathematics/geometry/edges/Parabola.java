package de.bioforscher.singa.mathematics.geometry.edges;

import de.bioforscher.singa.mathematics.vectors.Vector2D;

import java.util.*;

/**
 * A parabola is a two-dimensional, mirror-symmetrical curve, which is
 * approximately U-shaped. <br>
 * One description of a parabola involves a point (the focus) and a line (the
 * directrix). The focus does not lie on the directrix. The parabola is the set
 * of points in that plane that are equidistant from both the directrix and the
 * focus.
 *
 * @author cl
 * @see <a href="https://en.wikipedia.org/wiki/Parabola">Wikipedia: Parabola</a>
 */
public class Parabola {

    private final Vector2D focus;
    private final Line directrix;

    private double a = Double.NaN;
    private double b = Double.NaN;
    private double c = Double.NaN;

    /**
     * Creates a new Parabola given a focus and the directrix.
     *
     * @param focus The focus.
     * @param directrix The directrix.
     */
    public Parabola(Vector2D focus, Line directrix) {
        this.focus = focus;
        this.directrix = directrix;
        prepareFormulaForm();
    }

    /**
     * Gets the directrix.
     *
     * @return The directrix
     */
    public Line getDirectrix() {
        return directrix;
    }

    /**
     * Gets the focus.
     *
     * @return The focus.
     */
    public Vector2D getFocus() {
        return focus;
    }


    private void prepareFormulaForm() {
        if (Double.isNaN(a)) {
            final Vector2D vertex = getVertex();
            double p = -vertex.distanceTo(focus);
            if (!isOpenTowardsXAxis()) {
                p *= -1;
            }
            a = 1 / (4 * p);
            b = -vertex.getX() / (2 * p);
            c = vertex.getX() * vertex.getX() / (4 * p) + vertex.getY();
        }
    }

    /**
     * Gets the left intersection with another parabola.
     * <p>
     * TODO: simplify TODO: make a more general method which catches degenerate
     * chases FIXME: duplicated code
     *
     * @param parabola The other parabola.
     * @return The x-value of the intersection.
     */
    public List<Double> getInterceptsWithParabola(Parabola parabola) {

        List<Double> results = new ArrayList<>();

        // if both foci are on the directrix
        if (focus.getY() == directrix.getYIntercept()
                && parabola.getFocus().getY() == parabola.getDirectrix().getYIntercept()) {
            // no solutions
            return results;
        } else if (focus.getY() == directrix.getYIntercept()) {
            // if focus of this parabola in on directrix
            // one trivial intercept
            results.add(focus.getX());
            return results;
        } else if (parabola.getFocus().getY() == parabola.getDirectrix().getYIntercept()) {
            // if focus of the other parabola is on its directrix
            // one trivial intercept
            results.add(parabola.focus.getX());
            return results;
        }

        // if both foci are equidistant
        if (focus.distanceTo(directrix) == parabola.getFocus().distanceTo(parabola.getFocus())) {
            // one focus is in the middle between both foci
            results.add((focus.getX() - parabola.getFocus().getX()) / 2);
            return results;
        }

        double h2 = parabola.getVertex().getX();
        double k2 = parabola.getVertex().getY();
        double p2 = -parabola.getVertex().distanceTo(parabola.getFocus());

        double a2 = 1 / (4 * p2);
        double b2 = -h2 / (2 * p2);
        double c2 = h2 * h2 / (4 * p2) + k2;

        results.add((-(b - b2) + Math.sqrt((b - b2) * (b - b2) - 4 * (a - a2) * (c - c2))) /
                (2 * (a - a2)));
        results.add((-(b - b2) - Math.sqrt((b - b2) * (b - b2) - 4 * (a - a2) * (c - c2))) /
                (2 * (a - a2)));

        return results;
    }

    public SortedSet<Vector2D> getIntercepts(Line line) {
        SortedSet<Vector2D> intercepts = new TreeSet<>(Comparator.comparing(Vector2D::getX));
        if (line.isVertical()) {
            double x = line.getXIntercept();
            intercepts.add(new Vector2D(x, getYValue(x)));
        } else {
            double x1 = (-this.b + line.getSlope() + Math.sqrt(
                    4 * this.a * line.getYIntercept() + this.b * this.b - 4 * this.a * this.c -
                            2 * this.b * line.getSlope() + line.getSlope() * line.getSlope())) / (2 * this.a);
            double x2 = (-this.b + line.getSlope() - Math.sqrt(
                    4 * this.a * line.getYIntercept() + this.b * this.b - 4 * this.a * this.c -
                            2 * this.b * line.getSlope() + line.getSlope() * line.getSlope())) / (2 * this.a);
            intercepts.add(new Vector2D(x1, line.getYValue(x1)));
            intercepts.add(new Vector2D(x2, line.getYValue(x2)));
        }
        return intercepts;
    }

    public SortedSet<Double> getXIntercepts() {
        SortedSet<Double> intercepts = new TreeSet<>();
        intercepts.add((-b - Math.sqrt(b * b - 4 * a * c)) / (2 * a));
        intercepts.add((-b + Math.sqrt(b * b - 4 * a * c)) / (2 * a));
        return intercepts;
    }

    public boolean isOpenTowardsXAxis() {
        return focus.getY() < directrix.getYIntercept();
    }

    /**
     * Gets the vertex. <br>
     * <br>
     * The line perpendicular to the directrix and passing through the focus
     * (that is, the line that splits the parabola through the middle) is called
     * the "axis of symmetry". The point on the parabola that intersects the
     * axis of symmetry is called the "vertex", and is the point where the
     * parabola is most sharply curved.
     *
     * @return The vertex of this parabola.
     */
    public Vector2D getVertex() {
        return new Vector2D(focus.getX(), (focus.getY() + directrix.getYIntercept()) * 0.5);
    }

    /**
     * Gets the y-value in respect to a given x-value.
     * <p>
     * TODO: simplify FIXME: duplicated code
     *
     * @param x The x-value.
     * @return The y-value.
     */
    public double getYValue(double x) {
        return a * x * x + b * x + c;
    }

    @Override
    public String toString() {
        return "Parabola [focus=" + focus + ", directrix=" + directrix + "]";
    }

}
