package bio.singa.simulation.model.modules.macroscopic.filaments;

import bio.singa.mathematics.geometry.edges.Line;
import bio.singa.mathematics.geometry.faces.Circle;
import bio.singa.mathematics.geometry.model.Polygon;
import bio.singa.mathematics.vectors.Vector2D;
import bio.singa.mathematics.vectors.Vectors;
import bio.singa.simulation.model.graphs.AutomatonGraph;
import bio.singa.simulation.model.graphs.AutomatonNode;

import java.util.*;

import static bio.singa.mathematics.geometry.model.Polygon.ON_LINE;
import static bio.singa.mathematics.metrics.model.VectorMetricProvider.EUCLIDEAN_METRIC;
import static bio.singa.simulation.model.modules.macroscopic.filaments.SkeletalFilament.FilamentBehaviour.GROW;
import static bio.singa.simulation.model.modules.macroscopic.filaments.SkeletalFilament.FilamentBehaviour.STAGNANT;

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
    private AutomatonGraph graph;

    private Set<AutomatonNode> associatedNodes;

    SkeletalFilament(Vector2D initialPosition, Vector2D initialDirection, AutomatonGraph graph) {
        minusEndBehaviour = STAGNANT;
        plusEndBehaviour = GROW;
        this.graph = graph;
        segments = new LinkedList<>();
        associatedNodes = new HashSet<>();
        segments.add(initialPosition);
        segments.add(initialDirection.normalize().add(initialPosition));
    }

    private Vector2D getPreviousSegment(Vector2D segment) {
        int index = segments.indexOf(segment);
        return segments.get(index + 1);
    }

    public ListIterator<Vector2D> getSegmentIterator(Vector2D segment) {
        int index = segments.indexOf(segment);
        return segments.listIterator(index);
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
        Vector2D nextPosition = segments.getFirst().add(head);
        associateNodes(nextPosition);
        segments.addFirst(nextPosition);
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
        Vector2D nextPosition = segments.getFirst().add(nextSegment);
        associateNodes(nextPosition);
        segments.addFirst(nextPosition);
    }

    private Vector2D computeNextSegment(Vector2D head) {
        // r_n+1 = (r_n * (1 - r_d) + r_d * u) / mag(r_n * (1 - r_d) + r_d * u)
        return head.multiply(1 - rd).add(Vectors.generateRandomUnit2DVector().multiply(rd)).normalize().multiply(2);
    }

    private void associateNodes(Vector2D segment) {
        associatedNodes.clear();
        Circle headRegion = new Circle(segment, 10);
        // determine associated nodes
        for (AutomatonNode node : graph.getNodes()) {
            // get representative region of the node
            Polygon polygon = node.getSpatialRepresentation();
            // associate segment to the node with the largest part of the vesicle (midpoint is inside)
            if (polygon.evaluatePointPosition(segment) >= ON_LINE) {
                node.addMicrotubuleSegment(this, segment);
                associatedNodes.add(node);
            }
            // associate partial containment to other nodes
            if (!polygon.getIntersections(headRegion).isEmpty()) {
                node.addMicrotubuleSegment(this, segment);
                associatedNodes.add(node);
            }
        }
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

    Map.Entry<SkeletalFilament, Double> getClosestRelevantDistance() {
        Vector2D head = segments.getFirst();

        SkeletalFilament closestFilament = null;
        double closestDistance = Double.MAX_VALUE;
        // get closest relevant node
        for (AutomatonNode associatedNode : associatedNodes) {
            // get the associated segments
            for (Map.Entry<SkeletalFilament, Set<Vector2D>> entry : associatedNode.getMicrotubuleSegments().entrySet()) {
                SkeletalFilament currentFilament = entry.getKey();
                // don't compare to fragments in the same fragment
                if (currentFilament != this) {
                    Set<Vector2D> segments = entry.getValue();
                    // check each segment
                    for (Vector2D segment : segments) {
                        double currentDistance = head.distanceTo(segment);
                        if (currentDistance < closestDistance) {
                            closestDistance = currentDistance;
                            closestFilament = currentFilament;
                        }
                    }
                }
            }
        }

        return new AbstractMap.SimpleEntry<>(closestFilament, closestDistance);
    }

    public LinkedList<Vector2D> getSegments() {
        return segments;
    }

    public Vector2D getHead() {
        return segments.getFirst();
    }

    public Set<AutomatonNode> getAssociatedNodes() {
        return associatedNodes;
    }

    Vector2D getPlusEnd() {
        return segments.getFirst();
    }

    public FilamentBehaviour getPlusEndBehaviour() {
        return plusEndBehaviour;
    }

    void setPlusEndBehaviour(FilamentBehaviour plusEndBehaviour) {
        this.plusEndBehaviour = plusEndBehaviour;
    }

    void setLeadFilament(SkeletalFilament lead) {
        this.lead = lead;
    }
}
