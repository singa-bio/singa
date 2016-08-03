package de.bioforscher.simulation.util;

import de.bioforscher.mathematics.forces.AttractiveForce;
import de.bioforscher.mathematics.forces.Force;
import de.bioforscher.mathematics.forces.RepulsiveForce;
import de.bioforscher.mathematics.functions.DecayFunctions;
import de.bioforscher.mathematics.vectors.Vector2D;
import de.bioforscher.simulation.application.components.SimulationSpace;
import de.bioforscher.simulation.model.AutomatonGraph;
import de.bioforscher.simulation.model.BioEdge;
import de.bioforscher.simulation.model.BioNode;

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
public class GraphDrawingTool {

    private final int totalIterations;

    private Force repulsiveForce;
    private Force attractiveForce;
    private Force boundaryForce;

    private AutomatonGraph sourceGraph;
    private HashMap<BioNode, Vector2D> velocities;

    /**
     * Creates a new GraphDrawingTool.
     *
     * @param totalIterations Number of total iterations
     * @param startGraph The graph to arrange
     */
    public GraphDrawingTool(int totalIterations, AutomatonGraph startGraph) {
        // total iterations
        this.totalIterations = totalIterations;
        // the unordered graph
        this.sourceGraph = startGraph;
        // Forceconstant = sqrt(drawing area / desired area per node)
        double forceConstant = Math.sqrt((SimulationSpace.getInstance().getHeight().doubleValue() * SimulationSpace
                .getInstance().getWidth().doubleValue()) / (startGraph.getNodes().size() * 20));
        // repulsive force between nodes
        this.repulsiveForce = new RepulsiveForce(forceConstant);
        // repulsive force from boundaries
        this.boundaryForce = new RepulsiveForce(forceConstant * 2);
        // attractive force between nodes
        this.attractiveForce = new AttractiveForce(forceConstant);
        // temporary velocities
        this.velocities = new HashMap<>();
        for (BioNode n : startGraph.getNodes()) {
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
    public AutomatonGraph arrangeGraph(int i) {

        // calculate the temperature
        double t = DecayFunctions.linear(i, this.totalIterations, SimulationSpace.getInstance().getWidth().doubleValue() / 40);

        // calculate repulsive forces
        for (BioNode sourceNode : this.sourceGraph.getNodes()) {
            // reset velocities
            this.velocities.put(sourceNode, new Vector2D());
            for (BioNode targetNode : this.sourceGraph.getNodes()) {
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
        for (BioEdge edge : this.sourceGraph.getEdges()) {

            // get source and target of an edge
            BioNode sourceNode = edge.getSource();
            BioNode targetNode = edge.getTarget();

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
        for (BioNode node : this.sourceGraph.getNodes()) {

            // size of the barrier
            Vector2D position = node.getPosition();
            double barrierRadius = SimulationSpace.getInstance().getWidth().doubleValue() / 4.0;

            // calculate west and east barrier forces
            Vector2D accelerationX;
            if (position.getX() < barrierRadius) {
                // calculate west barrier repulsive acceleration
                accelerationX = this.boundaryForce.calculateAcceleration(position, new Vector2D(0, position.getY()));
            } else if (position.getX() > SimulationSpace.getInstance().getWidth().doubleValue() - barrierRadius) {
                // calculate east barrier repulsive acceleration
                accelerationX = this.boundaryForce.calculateAcceleration(position,
                        new Vector2D(SimulationSpace.getInstance().getWidth().doubleValue(), position.getY()));
            } else {
                // if not within barrier range
                accelerationX = new Vector2D(0, 0);
            }

            // calculate north and south barrier forces
            Vector2D accelerationY;
            if (position.getY() < barrierRadius) {
                // calculate north barrier repulsive acceleration
                accelerationY = this.boundaryForce.calculateAcceleration(position, new Vector2D(position.getX(), 0));
            } else if (position.getY() > SimulationSpace.getInstance().getHeight().doubleValue() - barrierRadius) {
                // calculate south barrier repulsive acceleration
                accelerationY = this.boundaryForce.calculateAcceleration(position,
                        new Vector2D(position.getX(), SimulationSpace.getInstance().getHeight().doubleValue()));
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
        for (BioNode node : this.sourceGraph.getNodes()) {

            Vector2D currentLocation = node.getPosition();
            Vector2D currentVelocity = this.velocities.get(node);
            double magnitude = currentVelocity.getMagnitude();

            // calculate new position v = v.pos + v^ * min(|v|,temp)
            Vector2D nextLocation = currentLocation.add(currentVelocity.normalize().multiply(Math.min(magnitude, t)));

            // taking care, that the vertices aren't placed outside of the
            // dimension
            // TODO: could be better
            double nextX;
            if (nextLocation.getX() < SimulationSpace.getInstance().getWidth().doubleValue() && nextLocation.getX() > 0.0) {
                nextX = nextLocation.getX();
            } else {
                nextX = SimulationSpace.getInstance().getWidth().doubleValue()/ 2;
            }

            double nextY;
            if (nextLocation.getY() < SimulationSpace.getInstance().getHeight().doubleValue() && nextLocation.getY() > 0.0) {
                nextY = nextLocation.getY();
            } else {
                nextY = SimulationSpace.getInstance().getHeight().doubleValue() / 2;
            }

            // place node
            node.setPosition(new Vector2D(nextX, nextY));

        }

        // returns the optimized graph
        return this.sourceGraph;

    }

}
