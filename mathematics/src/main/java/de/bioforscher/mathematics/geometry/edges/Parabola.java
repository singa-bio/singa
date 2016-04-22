package de.bioforscher.mathematics.geometry.edges;

import de.bioforscher.mathematics.vectors.Vector2D;

import java.util.ArrayList;
import java.util.List;

/**
 * A parabola is a two-dimensional, mirror-symmetrical curve, which is
 * approximately U-shaped. <br>
 * <br>
 * <p>
 * TODO is everything turned upside down?!
 * <p>
 * One description of a parabola involves a point (the focus) and a line (the
 * directrix). The focus does not lie on the directrix. The parabola is the set
 * of points in that plane that are equidistant from both the directrix and the
 * focus.
 *
 * @author Christoph Leberecht
 * @version 1.0.0
 * @see <a href="https://en.wikipedia.org/wiki/Parabola">Wikipedia: Parabola</a>
 */
public class Parabola {

    /**
     * The focus-point of the Parabola.
     */
    private Vector2D focus;

    /**
     * The directrix of the Parabola.
     */
    private HorizontalLine directrix;

    /**
     * Creates a new Parabola given a focus and the directrix.
     *
     * @param focus     The focus.
     * @param directrix The directrix.
     */
    public Parabola(Vector2D focus, HorizontalLine directrix) {
        super();
        this.focus = focus;
        this.directrix = directrix;
    }

    /**
     * Gets the directrix.
     *
     * @return The directrix
     */
    public HorizontalLine getDirectrix() {
        return this.directrix;
    }

    /**
     * Gets the focus.
     *
     * @return The focus.
     */
    public Vector2D getFocus() {
        return this.focus;
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
        if (this.focus.getY() == this.directrix.getPosition()
                && parabola.getFocus().getY() == parabola.getDirectrix().getPosition()) {
            // no solutions
            return results;
        } else if (this.focus.getY() == this.directrix.getPosition()) {
            // if focus of this parabola in on directrix
            // one trivial intercept
            results.add(this.focus.getX());
            return results;
        } else if (parabola.getFocus().getY() == parabola.getDirectrix().getPosition()) {
            // if focus of the other parabola is on its directrix
            // one trivial intercept
            results.add(parabola.focus.getX());
            return results;
        }

        // if both foci are equidistant
        if (this.focus.distanceTo(this.directrix) == parabola.getFocus().distanceTo(parabola.getFocus())) {
            // one focus is in the middle between both foci
            results.add((this.focus.getX() - parabola.getFocus().getX()) / 2);
            return results;
        }

        double h1 = this.getVertex().getX();
        double k1 = this.getVertex().getY();
        double p1 = -this.getVertex().distanceTo(this.focus);

        double a1 = 1 / (4 * p1);
        double b1 = -h1 / (2 * p1);
        double c1 = h1 * h1 / (4 * p1) + k1;

        double h2 = parabola.getVertex().getX();
        double k2 = parabola.getVertex().getY();
        double p2 = -parabola.getVertex().distanceTo(parabola.getFocus());

        double a2 = 1 / (4 * p2);
        double b2 = -h2 / (2 * p2);
        double c2 = h2 * h2 / (4 * p2) + k2;

        results.add((-(b1 - b2) + Math.sqrt((b1 - b2) * (b1 - b2) - 4 * (a1 - a2) * (c1 - c2))) / (2 * (a1 - a2)));
        results.add((-(b1 - b2) - Math.sqrt((b1 - b2) * (b1 - b2) - 4 * (a1 - a2) * (c1 - c2))) / (2 * (a1 - a2)));

        return results;
    }

    /**
     * Gets the left (smaller) intercept of the parabola with the x-axis.
     * <p>
     * TODO: simplify TODO: generalise (currently we assume parabolas are only
     * open towards the top) FIXME: duplicated code
     *
     * @return The left x-axis intercept.
     */
    public double getLeftXIntercept() {

        double h = this.getVertex().getX();
        double k = this.getVertex().getY();
        double p = -this.getVertex().distanceTo(this.focus);

        double a = 1 / (4 * p);
        double b = -h / (2 * p);
        double c = h * h / (4 * p) + k;

        return (-b + Math.sqrt(b * b - 4 * a * c)) / (2 * a);
    }

    /**
     * Gets the right (larger) intercept of the parabola with the x-axis.
     * <p>
     * TODO: simplify TODO: generalise (currently we assume parabolas are only
     * open towards the top) FIXME: duplicated code
     *
     * @return The right x-axis intercept.
     */
    public double getRightXIntercept() {
        double h = this.getVertex().getX();
        double k = this.getVertex().getY();
        double p = -this.getVertex().distanceTo(this.focus);

        double a = 1 / (4 * p);
        double b = -h / (2 * p);
        double c = h * h / (4 * p) + k;

        return (-b - Math.sqrt(b * b - 4 * a * c)) / (2 * a);
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
        return new Vector2D(this.focus.getX(), (this.focus.getY() + this.directrix.getPosition()) * 0.5);
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
        double h = this.getVertex().getX();
        double k = this.getVertex().getY();
        double p = -this.getVertex().distanceTo(this.focus);

        double a = 1 / (4 * p);
        double b = -h / (2 * p);
        double c = h * h / (4 * p) + k;

        return a * x * x + b * x + c;
    }

    /**
     * Sets the directrix.
     *
     * @param directrix The directrix.
     */
    public void setDirectrix(HorizontalLine directrix) {
        this.directrix = directrix;
    }

    /**
     * Sets the focus.
     *
     * @param focus The focus.
     */
    public void setFocus(Vector2D focus) {
        this.focus = focus;
    }

    @Override
    public String toString() {
        return "Parabola [focus=" + this.focus + ", directrix=" + this.directrix + "]";
    }

}
