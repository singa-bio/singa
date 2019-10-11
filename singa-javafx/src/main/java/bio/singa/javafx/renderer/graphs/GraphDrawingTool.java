package bio.singa.javafx.renderer.graphs;

import bio.singa.mathematics.forces.AttractiveForce;
import bio.singa.mathematics.forces.Force;
import bio.singa.mathematics.forces.RepulsiveForce;
import bio.singa.mathematics.functions.DecayFunctions;
import bio.singa.mathematics.graphs.model.Edge;
import bio.singa.mathematics.graphs.model.Graph;
import bio.singa.mathematics.graphs.model.Node;
import bio.singa.mathematics.vectors.Vector2D;
import javafx.beans.property.DoubleProperty;

import java.util.HashMap;

/**
 * This class tries to arrange a graph using force directed placement. <br>
 * The algorithm is based on Fruchterman, Thomas MJ, and Edward M. Reingold.
 * "Graph drawing by force-directed placement." Softw., Pract. Exper. 21.11
 * (1991): 1129-1164.<br>
 * With some modifications.
 *
 * @author cl
 */
public class GraphDrawingTool<NodeType extends Node<NodeType, Vector2D, IdentifierType>, EdgeType extends Edge<NodeType>,
        IdentifierType, GraphType extends Graph<NodeType, EdgeType, IdentifierType>> {

    private final int totalIterations;
    private final DoubleProperty drawingWidth;
    private final DoubleProperty drawingHeight;
    private final Force repulsiveForce;
    private final Force attractiveForce;
    private final Force boundaryForce;

    private final GraphType graph;
    private final HashMap<NodeType, Vector2D> velocities;

    /**
     * Creates a new GraphDrawingTool.
     *
     * @param totalIterations Number of total iterations
     * @param graph The graph to arrange
     * @param drawingHeight The height property.
     * @param drawingWidth The width property.
     */
    public GraphDrawingTool(GraphType graph, DoubleProperty drawingWidth, DoubleProperty drawingHeight, int totalIterations) {
        this.drawingWidth = drawingWidth;
        this.drawingHeight = drawingHeight;
        this.totalIterations = totalIterations;
        this.graph = graph;
        // force constant = sqrt(drawing area / desired area per node)
        double forceConstant = Math.sqrt((drawingHeight.get() * drawingWidth.get()) / (graph.getNodes().size() * 20));
        // repulsive force between nodes
        repulsiveForce = new RepulsiveForce(forceConstant);
        // repulsive force from boundaries
        boundaryForce = new RepulsiveForce(forceConstant * 2);
        // attractive force between nodes
        attractiveForce = new AttractiveForce(forceConstant);
        // temporary velocities
        velocities = new HashMap<>();
        for (NodeType n : graph.getNodes()) {
            velocities.put(n, new Vector2D(0.0, 0.0));
        }
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
        double t = DecayFunctions.linear(i, totalIterations, drawingWidth.doubleValue() / 40);

        // calculate repulsive forces
        for (NodeType sourceNode : graph.getNodes()) {
            // reset velocities
            velocities.put(sourceNode, new Vector2D());
            for (NodeType targetNode : graph.getNodes()) {
                // if source and target are different
                if (!sourceNode.equals(targetNode)) {
                    // calculate repulsive acceleration
                    Vector2D acceleration = repulsiveForce.calculateAcceleration(sourceNode.getPosition(),
                            targetNode.getPosition());
                    // add acceleration to current velocity
                    Vector2D velocity = velocities.get(sourceNode).add(acceleration);
                    velocities.put(sourceNode, velocity);
                }
            }
        }

        // calculate attractive forces
        for (EdgeType edge : graph.getEdges()) {

            // get source and target of an edge
            NodeType sourceNode = edge.getSource();
            NodeType targetNode = edge.getTarget();

            // calculate attractive acceleration
            Vector2D acceleration = attractiveForce.calculateAcceleration(sourceNode.getPosition(),
                    targetNode.getPosition());

            // add acceleration to targets's velocities
            Vector2D velocityTarget = velocities.get(targetNode).add(acceleration);
            velocities.put(targetNode, velocityTarget);

            // subtract acceleration to source's velocities (fling to opposite
            // direction)
            Vector2D velocitySource = velocities.get(sourceNode).subtract(acceleration);
            velocities.put(sourceNode, velocitySource);

        }

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

}
