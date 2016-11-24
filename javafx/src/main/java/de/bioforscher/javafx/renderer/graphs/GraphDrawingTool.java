package de.bioforscher.javafx.renderer.graphs;

import de.bioforscher.mathematics.forces.AttractiveForce;
import de.bioforscher.mathematics.forces.Force;
import de.bioforscher.mathematics.forces.RepulsiveForce;
import de.bioforscher.mathematics.functions.DecayFunctions;
import de.bioforscher.mathematics.graphs.model.Edge;
import de.bioforscher.mathematics.graphs.model.Graph;
import de.bioforscher.mathematics.graphs.model.Node;
import de.bioforscher.mathematics.vectors.Vector2D;
import javafx.scene.canvas.Canvas;

import java.util.HashMap;

/**
 * This class tries to arrange a graph using force directed placement. <br>
 * The algorithm is based on Fruchterman, Thomas MJ, and Edward M. Reingold.
 * "Graph drawing by force-directed placement." Softw., Pract. Exper. 21.11
 * (1991): 1129-1164.<br>
 * With some modifications.
 *
 * @author Christoph Leberecht
 * @version 1.0.1
 */
public class GraphDrawingTool<NodeType extends Node<NodeType, Vector2D>, EdgeType extends Edge<NodeType>,
        GraphType extends Graph<NodeType, EdgeType>> {

    private final int totalIterations;

    private Force repulsiveForce;
    private Force attractiveForce;
    private Force boundaryForce;

    private GraphType graph;
    private HashMap<NodeType, Vector2D> velocities;
    private Canvas canvas;

    /**
     * Creates a new GraphDrawingTool.
     *
     * @param totalIterations Number of total iterations
     * @param graph           The graph to arrange
     */
    public GraphDrawingTool(int totalIterations, GraphType graph, Canvas canvas) {
        this.totalIterations = totalIterations;
        this.graph = graph;
        this.canvas = canvas;
        // force constant = sqrt(drawing area / desired area per node)
        double forceConstant = Math.sqrt((canvas.getHeight() * canvas.getWidth()) / (graph.getNodes().size() * 20));
        // repulsive force between nodes
        this.repulsiveForce = new RepulsiveForce(forceConstant);
        // repulsive force from boundaries
        this.boundaryForce = new RepulsiveForce(forceConstant * 2);
        // attractive force between nodes
        this.attractiveForce = new AttractiveForce(forceConstant);
        // temporary velocities
        this.velocities = new HashMap<>();
        for (NodeType n : graph.getNodes()) {
            this.velocities.put(n, new Vector2D(0.0, 0.0));
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
        double t = DecayFunctions.linear(i, this.totalIterations, this.canvas.getWidth() / 40);

        // calculate repulsive forces
        for (NodeType sourceNode : this.graph.getNodes()) {
            // reset velocities
            this.velocities.put(sourceNode, new Vector2D());
            for (NodeType targetNode : this.graph.getNodes()) {
                // if source and target are different
                if (!sourceNode.equals(targetNode)) {
                    // calculate repulsive acceleration
                    Vector2D acceleration = this.repulsiveForce.calculateAcceleration(sourceNode.getPosition(),
                            targetNode.getPosition());
                    // add acceleration to current velocity
                    Vector2D velocity = this.velocities.get(sourceNode).add(acceleration);
                    this.velocities.put(sourceNode, velocity);
                }
            }
        }

        // calculate attractive forces
        for (EdgeType edge : this.graph.getEdges()) {

            // get source and target of an edge
            NodeType sourceNode = edge.getSource();
            NodeType targetNode = edge.getTarget();

            // calculate attractive acceleration
            Vector2D acceleration = this.attractiveForce.calculateAcceleration(sourceNode.getPosition(),
                    targetNode.getPosition());

            // add acceleration to targets's velocities
            Vector2D velocityTarget = this.velocities.get(targetNode).add(acceleration);
            this.velocities.put(targetNode, velocityTarget);

            // subtract acceleration to source's velocities (fling to opposite
            // direction)
            Vector2D velocitySource = this.velocities.get(sourceNode).subtract(acceleration);
            this.velocities.put(sourceNode, velocitySource);

        }

        // calculate repulsion from boundaries
        for (NodeType node : this.graph.getNodes()) {

            // size of the barrier
            Vector2D position = node.getPosition();
            double barrierRadius = this.canvas.getWidth() / 4.0;

            // calculate west and east barrier forces
            Vector2D accelerationX;
            if (position.getX() < barrierRadius) {
                // calculate west barrier repulsive acceleration
                accelerationX = this.boundaryForce.calculateAcceleration(position, new Vector2D(0, position.getY()));
            } else if (position.getX() > this.canvas.getWidth() - barrierRadius) {
                // calculate east barrier repulsive acceleration
                accelerationX = this.boundaryForce.calculateAcceleration(position,
                        new Vector2D(this.canvas.getWidth(), position.getY()));
            } else {
                // if not within barrier range
                accelerationX = new Vector2D(0, 0);
            }

            // calculate north and south barrier forces
            Vector2D accelerationY;
            if (position.getY() < barrierRadius) {
                // calculate north barrier repulsive acceleration
                accelerationY = this.boundaryForce.calculateAcceleration(position, new Vector2D(position.getX(), 0));
            } else if (position.getY() > this.canvas.getHeight() - barrierRadius) {
                // calculate south barrier repulsive acceleration
                accelerationY = this.boundaryForce.calculateAcceleration(position,
                        new Vector2D(position.getX(), this.canvas.getHeight()));
            } else {
                // if not within barrier range
                accelerationY = new Vector2D(0, 0);
            }

            // add acceleration to velocities
            Vector2D totalAcceleration = accelerationX.add(accelerationY);
            Vector2D velocitySource = this.velocities.get(node).add(totalAcceleration);
            this.velocities.put(node, velocitySource);

        }

        // placement depending on current velocity
        for (NodeType node : this.graph.getNodes()) {

            Vector2D currentLocation = node.getPosition();
            Vector2D currentVelocity = this.velocities.get(node);
            double magnitude = currentVelocity.getMagnitude();

            // calculate new position v = v.pos + v^ * min(|v|,temp)
            Vector2D nextLocation = currentLocation.add(currentVelocity.normalize().multiply(Math.min(magnitude, t)));

            // taking care, that the vertices aren't placed outside of the
            // dimension
            // TODO: could be better
            double nextX;
            if (nextLocation.getX() < this.canvas.getWidth() && nextLocation.getX() > 0.0) {
                nextX = nextLocation.getX();
            } else {
                nextX = this.canvas.getWidth() / 2;
            }

            double nextY;
            if (nextLocation.getY() < this.canvas.getHeight() && nextLocation.getY() > 0.0) {
                nextY = nextLocation.getY();
            } else {
                nextY = this.canvas.getHeight() / 2;
            }

            // place node
            node.setPosition(new Vector2D(nextX, nextY));

        }

        // returns the optimized graph
        return this.graph;

    }

}
