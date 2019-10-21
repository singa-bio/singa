package bio.singa.javafx.renderer.graphs;

import bio.singa.mathematics.functions.DecayFunctions;
import bio.singa.mathematics.graphs.model.Edge;
import bio.singa.mathematics.graphs.model.Graph;
import bio.singa.mathematics.graphs.model.Node;
import bio.singa.mathematics.vectors.Vector2D;
import javafx.beans.property.DoubleProperty;

import java.util.*;

/**
 * This class tries to arrange a graph using force directed placement. <br>
 * The algorithm is based on Fruchterman, Thomas MJ, and Edward M. Reingold.
 * "Graph drawing by force-directed placement." Softw., Pract. Exper. 21.11
 * (1991): 1129-1164.<br>
 * With some modifications.
 *
 * @author cl
 */
public class ForceDirectedGraphLayout<NodeType extends Node<NodeType, Vector2D, IdentifierType>, EdgeType extends Edge<NodeType>,
        IdentifierType, GraphType extends Graph<NodeType, EdgeType, IdentifierType>> implements LayoutRenderer<NodeType, EdgeType, IdentifierType, GraphType> {


    private final int totalIterations;
    private final DoubleProperty drawingWidth;
    private final DoubleProperty drawingHeight;
    private final GraphType graph;
    private final Map<NodeType, Vector2D> velocities;
    private int iteration;
    private List<Force<NodeType>> forces;
    private Collection<IdentifierType> fixedNodes;

    /**
     * Creates a new GraphDrawingTool.
     *
     * @param totalIterations Number of total iterations
     * @param graph The graph to arrange
     * @param drawingHeight The height property.
     * @param drawingWidth The width property.
     */
    public ForceDirectedGraphLayout(GraphType graph, DoubleProperty drawingWidth, DoubleProperty drawingHeight, int totalIterations) {
        this.drawingWidth = drawingWidth;
        this.drawingHeight = drawingHeight;
        this.totalIterations = totalIterations;
        this.graph = graph;
        // force constant = sqrt(drawing area / desired area per node)
        double forceConstant = Math.sqrt((drawingHeight.get() * drawingWidth.get()) / (graph.getNodes().size()));
        // temporary velocities
        velocities = new HashMap<>();
        for (NodeType n : graph.getNodes()) {
            velocities.put(n, new Vector2D(0.0, 0.0));
        }
        // repulsive force between nodes
        Force<NodeType> repulsiveForce = new RepulsiveForce<>((v1, v2) -> {
            // d = n1 - n2
            Vector2D distance = v1.getPosition().subtract(v2.getPosition());
            // m = |d|
            double magnitude = distance.getMagnitude();
            // v = unit(d) * force(m)
            //
            return distance.normalize().multiply((forceConstant * forceConstant) / magnitude).multiply(100);
        }, velocities);

        Force<NodeType> attractiveForce = new AttractiveForce<>((v1, v2) -> {
            if (!v1.getNeighbours().contains(v2)) {
                return Vector2D.ZERO;
            }
            // d = n1 - n2
            Vector2D distance = v1.getPosition().subtract(v2.getPosition());
            // m = |d|
            double magnitude = distance.getMagnitude();
            if (magnitude > 200) {
                return Vector2D.ZERO;
            }
            // v = unit(d) * force(m)
            // (magnitude * magnitude) / forceConstant
            return distance.normalize().multiply((magnitude * magnitude) / forceConstant);
        }, velocities);
//        attractiveForce.setAttractive(true);

        forces = new ArrayList<>();
        forces.add(repulsiveForce);
//        forces.add(attractiveForce);

        fixedNodes = new ArrayList<>();
    }

    public void fixNodes(Collection<IdentifierType> identifiers) {
        fixedNodes = identifiers;
    }

    @Override
    public GraphType optimizeLayout() {
        iteration++;
        return arrangeGraph(iteration);
    }

    /**
     * Calculates one iteration of the optimization process and returns the
     * resulting graph.
     *
     * @param i The current iteration.
     * @return The resulting graph
     */
    public GraphType arrangeGraph(int i) {

        // calculate the temperature
        double t = DecayFunctions.linear(i, totalIterations, drawingWidth.doubleValue() / 200);
        applyForce();

        // calculate repulsion from boundaries
//        for (NodeType node : graph.getNodes()) {
//
//            // size of the barrier
//            Vector2D position = node.getPosition();
//            double barrierRadius = drawingWidth.doubleValue() / 15.0;
//
//            // calculate west and east barrier forces
//            Vector2D accelerationX;
//            if (position.getX() < barrierRadius) {
//                // calculate west barrier repulsive acceleration
//                accelerationX = boundaryForce.calculateAcceleration(position, new Vector2D(0, position.getY()));
//            } else if (position.getX() > drawingWidth.doubleValue() - barrierRadius) {
//                // calculate east barrier repulsive acceleration
//                accelerationX = boundaryForce.calculateAcceleration(position,
//                        new Vector2D(drawingWidth.doubleValue(), position.getY()));
//            } else {
//                // if not within barrier range
//                accelerationX = new Vector2D(0, 0);
//            }
//
//            // calculate north and south barrier forces
//            Vector2D accelerationY;
//            if (position.getY() < barrierRadius) {
//                // calculate north barrier repulsive acceleration
//                accelerationY = boundaryForce.calculateAcceleration(position, new Vector2D(position.getX(), 0));
//            } else if (position.getY() > drawingHeight.doubleValue() - barrierRadius) {
//                // calculate south barrier repulsive acceleration
//                accelerationY = boundaryForce.calculateAcceleration(position,
//                        new Vector2D(position.getX(), drawingHeight.doubleValue()));
//            } else {
//                // if not within barrier range
//                accelerationY = new Vector2D(0, 0);
//            }
//
//            // add acceleration to velocities
//            Vector2D totalAcceleration = accelerationX.add(accelerationY);
//            Vector2D velocitySource = velocities.get(node).add(totalAcceleration);
//            velocities.put(node, velocitySource);
//
//        }

        // placement depending on current velocity
        for (NodeType node : graph.getNodes()) {

            if (fixedNodes == null || fixedNodes.contains(node.getIdentifier())) {
                continue;
            }

            Vector2D currentLocation = node.getPosition();
            Vector2D currentVelocity = velocities.get(node);
            double magnitude = currentVelocity.getMagnitude();

            // calculate new position v = v.pos + v^ * min(|v|,temp)
            Vector2D nextLocation = currentLocation.add(currentVelocity.normalize().multiply(Math.min(magnitude, t)));

            // taking care, that the vertices aren't placed outside of the
            // dimension
            // TODO: could be better
            double nextX;
            if (nextLocation.getX() < drawingWidth.doubleValue() && nextLocation.getX() > 0.0) {
                nextX = nextLocation.getX();
            } else {
                nextX = drawingWidth.doubleValue() / 2 + Math.random() - 0.5;
            }

            double nextY;
            if (nextLocation.getY() < drawingHeight.doubleValue() && nextLocation.getY() > 0.0) {
                nextY = nextLocation.getY();
            } else {
                nextY = drawingHeight.doubleValue() / 2 + Math.random() - 0.5;
            }

            // place node
            node.setPosition(new Vector2D(nextX, nextY));

        }

        // returns the optimized graph
        return graph;

    }

    private void applyForce() {
        List<NodeType> nodes = new ArrayList<>(graph.getNodes());
        velocities.clear();
        Vector2D[][] compactValues = new Vector2D[nodes.size()][];
        for (int rowIndex = 0; rowIndex < nodes.size(); rowIndex++) {
            compactValues[rowIndex] = new Vector2D[rowIndex + 1];
        }
        // compute forces
        for (int rowIndex = 0; rowIndex < compactValues.length; rowIndex++) {
            for (int columnIndex = 0; columnIndex < compactValues[rowIndex].length-1; columnIndex++) {

                NodeType source = nodes.get(rowIndex);
                NodeType target = nodes.get(columnIndex);

                if (source == target) {
                    continue;
                }
                for (Force<NodeType> force : forces) {
                    // calculate accelerations
                    force.calculateAcceleration(source, target);
                }
            }
            System.out.println();
        }
        System.out.println();
    }

    private void applyAcceleration(Vector2D acceleration, NodeType node, boolean attractive) {
        // add acceleration to current velocity
        Vector2D velocity = velocities.get(node);
        if (velocity == null) {
            velocity = new Vector2D();
        }
        if (attractive) {
            System.out.println(node + " - " + acceleration);
            velocities.put(node, velocity.subtract(acceleration));
        } else {
            System.out.println(node + " - " + acceleration);
            velocities.put(node, velocity.add(acceleration));
        }
    }


}
