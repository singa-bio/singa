package de.bioforscher.singa.simulation.model.modules.macroscopic;

import de.bioforscher.singa.mathematics.geometry.edges.Line;
import de.bioforscher.singa.mathematics.vectors.Vector2D;
import de.bioforscher.singa.mathematics.vectors.Vectors;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import static de.bioforscher.singa.mathematics.metrics.model.VectorMetricProvider.EUCLIDEAN_METRIC;
import static de.bioforscher.singa.simulation.model.modules.macroscopic.SkeletalFilament.FilamentBehaviour.GROW;
import static de.bioforscher.singa.simulation.model.modules.macroscopic.SkeletalFilament.FilamentBehaviour.STAGNANT;

/**
 * Modified: Mirabet, Vincent, et al. "The self-organization of plant microtubules inside the cell volume yields their
 * cortical localization, stable alignment, and sensitivity to external cues." PLoS computational biology 14.2 (2018):
 * e1006011.
 *
 * @author cl
 */
public class SkeletalFilament {

    public enum FilamentBehaviour {
        GROW, SHRINK, STAGNANT, FOLLOW
    }

    /**
     * Modifier for the randomness of the direction.
     */
    private static final double rd = 0.05;

    /**
     * Plus side is front, Minus side is back.
     */
    private LinkedList<Vector2D> segments;

    private SkeletalFilament lead;

    private FilamentBehaviour minusEndBehaviour;
    private FilamentBehaviour plusEndBehaviour;

    public SkeletalFilament() {
        segments = new LinkedList<>();
        minusEndBehaviour = STAGNANT;
        plusEndBehaviour = GROW;
    }

    public SkeletalFilament(Vector2D initialPosition, Vector2D initialDirection) {
        this();
        segments.add(initialPosition);
        segments.add(initialDirection.normalize().add(initialPosition));
    }

    private Vector2D getPreviousSegment(Vector2D vector2D) {
        int index = segments.indexOf(vector2D);
        return segments.get(index + 1);
    }

    public int nextEpoch() {
        switch (minusEndBehaviour) {
            case SHRINK:
                shrinkMinus();
                break;
            default:
                // do nothing
        }
        switch (plusEndBehaviour) {
            case GROW:
                growPlus();
                break;
            case SHRINK:
                shrinkPlus();
                break;
            case FOLLOW:
                if (segments.size() > 2) {
                    follow();
                } else {
                    growPlus();
                }
                break;
            case STAGNANT:
                // do nothing
                break;
        }
        return segments.size();
    }

    private void follow() {
        Map.Entry<Vector2D, Double> closestFragmentEntry = EUCLIDEAN_METRIC.calculateClosestDistance(lead.getSegments(), segments.getFirst());
        Vector2D closestFragment = closestFragmentEntry.getKey();
        Vector2D previousSegment = lead.getPreviousSegment(closestFragment);
        Vector2D head = closestFragment.subtract(previousSegment);
        segments.addFirst(segments.getFirst().add(head));
    }

    private void growPlus() {
        Vector2D head;
        if (segments.size() == 1) {
            // this is the first growth
            // normalize
            head = segments.getFirst().normalize();
        } else {
            // any subsequent growth
            // current - previous
            Iterator<Vector2D> iterator = segments.iterator();
            head = iterator.next().subtract(iterator.next());
        }
        Vector2D nextSegment = computeNextSegment(head);
        segments.addFirst(segments.getFirst().add(nextSegment));
    }

    private Vector2D computeNextSegment(Vector2D head) {
        // r_n+1 = (r_n * (1 - r_d) + r_d * u) / mag(r_n * (1 - r_d) + r_d * u)
        return head.multiply(1 - rd).add(Vectors.generateRandomUnit2DVector().multiply(rd)).normalize().multiply(2);
    }

    private void shrinkMinus() {
        segments.removeLast();
    }

    private void shrinkPlus() {
        segments.removeFirst();
    }

    double angleTo(SkeletalFilament filament) {
        Iterator<Vector2D> thisSegments = segments.iterator();
        Line thisLine = new Line(thisSegments.next(), thisSegments.next());
        Iterator<Vector2D> otherSegments = filament.getSegments().iterator();
        Line otherLine = new Line(otherSegments.next(), otherSegments.next());
        return thisLine.getAngleTo(otherLine);
    }

    double distanceTo(SkeletalFilament filament) {
        Vector2D head = segments.getFirst();
        Map.Entry<Vector2D, Double> closestFragment = EUCLIDEAN_METRIC.calculateClosestDistance(filament.getSegments(), head);
        return closestFragment.getValue();
    }

    public LinkedList<Vector2D> getSegments() {
        return segments;
    }

    Vector2D getPlusEnd() {
        return segments.getFirst();
    }

    FilamentBehaviour getPlusEndBehaviour() {
        return plusEndBehaviour;
    }

    void setPlusEndBehaviour(FilamentBehaviour plusEndBehaviour) {
        this.plusEndBehaviour = plusEndBehaviour;
    }

    void setLeadFilament(SkeletalFilament lead) {
        this.lead = lead;
    }
}
