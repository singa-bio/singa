package bio.singa.simulation.model.agents.linelike;

import bio.singa.mathematics.geometry.edges.Line;
import bio.singa.mathematics.geometry.edges.VectorPath;
import bio.singa.mathematics.geometry.faces.Circle;
import bio.singa.mathematics.geometry.model.Polygon;
import bio.singa.mathematics.topology.grids.rectangular.NeumannRectangularDirection;
import bio.singa.mathematics.vectors.Vector2D;
import bio.singa.mathematics.vectors.Vectors;
import bio.singa.simulation.model.graphs.AutomatonGraph;
import bio.singa.simulation.model.graphs.AutomatonNode;

import java.util.*;

import static bio.singa.mathematics.metrics.model.VectorMetricProvider.EUCLIDEAN_METRIC;
import static bio.singa.simulation.model.agents.linelike.LineLikeAgent.GrowthBehaviour.GROW;
import static bio.singa.simulation.model.agents.linelike.LineLikeAgent.GrowthBehaviour.STAGNANT;

/**
 * @author cl
 */
public class LineLikeAgent {

    public enum GrowthBehaviour {
        GROW, SHRINK, STAGNANT, FOLLOW
    }

    public static final String ACTIN = "ACTIN";
    public static final String MICROTUBULE = "MICROTUBULE";

    /**
     * Modifier for the randomness of the direction.
     */
    private static double rd = 0.05;

    private VectorPath path;
    private String type;
    private Set<AutomatonNode> associatedNodes;
    private AutomatonGraph graph;

    /**
     * Plus side is front, Minus side is back.
     */
    private LineLikeAgent lead;
    private GrowthBehaviour minusEndBehaviour;
    private GrowthBehaviour plusEndBehaviour;

    public LineLikeAgent(String type) {
        this.type = type;
        path = new VectorPath();
        associatedNodes = new HashSet<>();
    }

    public LineLikeAgent(String type, Vector2D initialPosition, Vector2D initialDirection, AutomatonGraph graph) {
        this(type);
        setGraph(graph);
        minusEndBehaviour = STAGNANT;
        plusEndBehaviour = GROW;
        path.addToTail(initialPosition);
        path.addToTail(initialDirection.normalize().add(initialPosition));
    }

    public LineLikeAgent(String type, List<Vector2D> segments, NeumannRectangularDirection plusDirection) {
        this(type);
        if (orderingIsReversed(segments, plusDirection)) {
            Collections.reverse(segments);
        }
        setPath(new VectorPath(segments));
    }

    public VectorPath getPath() {
        return path;
    }

    public void setPath(VectorPath path) {
        this.path = path;
    }

    public AutomatonGraph getGraph() {
        return graph;
    }

    public void setGraph(AutomatonGraph graph) {
        this.graph = graph;
    }

    public Set<AutomatonNode> getAssociatedNodes() {
        return associatedNodes;
    }

    public void setAssociatedNodes(Set<AutomatonNode> associatedNodes) {
        this.associatedNodes = associatedNodes;
    }

    public Vector2D getPlusEnd() {
        return path.getHead();
    }

    public Vector2D getMinusEnd() {
        return path.getTail();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    Vector2D getPreviousSegmentOf(Vector2D segment) {
        int index = path.getIndexOf(segment);
        return path.getVectorAt(index + 1);
    }

    public GrowthBehaviour getPlusEndBehaviour() {
        return plusEndBehaviour;
    }

    void setPlusEndBehaviour(GrowthBehaviour plusEndBehaviour) {
        this.plusEndBehaviour = plusEndBehaviour;
    }

    public GrowthBehaviour getMinusEndBehaviour() {
        return minusEndBehaviour;
    }

    public void setMinusEndBehaviour(GrowthBehaviour minusEndBehaviour) {
        this.minusEndBehaviour = minusEndBehaviour;
    }

    void setLeadAgent(LineLikeAgent lead) {
        this.lead = lead;
    }

    boolean orderingIsReversed(List<Vector2D> segments, NeumannRectangularDirection plusDirection) {
        Vector2D front = segments.iterator().next();
        Vector2D back = segments.get(segments.size() - 1);
        switch (plusDirection) {
            case NORTH:
                return front.isBelow(back);
            case SOUTH:
                return front.isAbove(back);
            case EAST:
                return front.isLeftOf(back);
            default:
                return front.isRightOf(back);
        }
    }

    public void associateInGraph(AutomatonGraph graph) {
        setGraph(graph);
        for (Vector2D segment : getPath().getSegments()) {
            Circle headRegion = new Circle(segment, 10);
            // determine associated nodes
            for (AutomatonNode node : graph.getNodes()) {
                // get representative region of the node
                Polygon polygon = node.getSpatialRepresentation();
                // associate segment to the node with the largest part of the vesicle (midpoint is inside)
                if (polygon.isInside(segment)) {
                    node.addLineLikeAgentSegment(this, segment);
                }
                // associate partial containment to other nodes
                if (!polygon.getIntersections(headRegion).isEmpty()) {
                    node.addLineLikeAgentSegment(this, segment);
                }
            }
        }
    }

    public int nextEpoch() {
        switch (minusEndBehaviour) {
            case SHRINK:
                shrinkMinus();
                break;
            default:
                // do nothing
                break;
        }
        switch (plusEndBehaviour) {
            case GROW:
                growPlus();
                break;
            case SHRINK:
                shrinkPlus();
                break;
            case FOLLOW:
                if (path.size() > 2) {
                    follow();
                } else {
                    growPlus();
                }
                break;
            case STAGNANT:
                // do nothing
                break;
        }
        return path.size();
    }

    private void follow() {
        Map.Entry<Vector2D, Double> closestFragmentEntry = EUCLIDEAN_METRIC.calculateClosestDistance(lead.getPath().getSegments(), path.getHead());
        Vector2D closestFragment = closestFragmentEntry.getKey();
        Vector2D previousSegment = lead.getPreviousSegmentOf(closestFragment);
        Vector2D head = closestFragment.subtract(previousSegment);
        Vector2D nextPosition = path.getHead().add(head);
        associateNodes(nextPosition);
        path.addToHead(nextPosition);
    }

    private void growPlus() {
        Vector2D head;
        if (path.size() == 1) {
            // this is the first growth
            // normalize
            head = path.getHead().normalize();
        } else {
            // any subsequent growth
            // current - previous
            Iterator<Vector2D> iterator = path.getSegments().iterator();
            head = iterator.next().subtract(iterator.next());
        }
        Vector2D nextSegment = computeNextSegment(head);
        Vector2D nextPosition = path.getHead().add(nextSegment);
        associateNodes(nextPosition);
        path.addToHead(nextPosition);
    }

    private void shrinkMinus() {
        path.removeTail();
    }

    private void shrinkPlus() {
        path.removeHead();
    }

    private Vector2D computeNextSegment(Vector2D head) {
        // r_n+1 = (r_n * (1 - r_d) + r_d * u) / mag(r_n * (1 - r_d) + r_d * u)
        return head.multiply(1 - rd).add(Vectors.generateRandomUnit2DVector().multiply(rd)).normalize().multiply(2);
    }

    private void associateNodes(Vector2D segment) {
        associatedNodes.clear();
        Circle headRegion = new Circle(segment, 10);
        // determine associated nodes
        for (AutomatonNode node : getGraph().getNodes()) {
            // get representative region of the node
            Polygon polygon = node.getSpatialRepresentation();
            // associate segment to the node with the largest part of the vesicle (midpoint is inside)
            if (polygon.isInside(segment)) {
                node.addLineLikeAgentSegment(this, segment);
                associatedNodes.add(node);
            }
            // associate partial containment to other nodes
            if (!polygon.getIntersections(headRegion).isEmpty()) {
                node.addLineLikeAgentSegment(this, segment);
                associatedNodes.add(node);
            }
        }
    }

    double angleTo(LineLikeAgent filament) {
        Iterator<Vector2D> thisSegments = path.getSegments().iterator();
        Line thisLine = new Line(thisSegments.next(), thisSegments.next());
        Iterator<Vector2D> otherSegments = filament.getPath().getSegments().iterator();
        Line otherLine = new Line(otherSegments.next(), otherSegments.next());
        return thisLine.getAngleTo(otherLine);
    }

    Map.Entry<LineLikeAgent, Double> getClosestRelevantDistance() {
        Vector2D head = path.getHead();
        LineLikeAgent closestFilament = null;
        double closestDistance = Double.MAX_VALUE;
        // get closest relevant node
        for (AutomatonNode associatedNode : associatedNodes) {
            // get the associated path
            for (Map.Entry<LineLikeAgent, Set<Vector2D>> entry : associatedNode.getAssociatedLineLikeAgents().entrySet()) {
                LineLikeAgent currentFilament = entry.getKey();
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

}
