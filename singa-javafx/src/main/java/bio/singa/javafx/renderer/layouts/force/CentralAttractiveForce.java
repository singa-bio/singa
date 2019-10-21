package bio.singa.javafx.renderer.layouts.force;

import bio.singa.mathematics.graphs.model.Node;
import bio.singa.mathematics.vectors.Vector2D;

import java.util.function.Predicate;

/**
 * @author cl
 */
public class CentralAttractiveForce<NodeType extends Node<NodeType, Vector2D, ?>> {

    private ForceDirectedGraphLayout<NodeType, ?, ?, ?> parentLayout;
    private Predicate<NodeType> forcePredicate;

    public CentralAttractiveForce(ForceDirectedGraphLayout<NodeType, ?, ?, ?> parentLayout) {
        this.parentLayout = parentLayout;
        forcePredicate = node -> true;
    }

    public Predicate<NodeType> getForcePredicate() {
        return forcePredicate;
    }

    public void setForcePredicate(Predicate<NodeType> forcePredicate) {
        this.forcePredicate = forcePredicate;
    }

    public void determineDisplacement(NodeType node) {
        if (!forcePredicate.test(node)) {
            return;
        }
        Vector2D acceleration = calculateAcceleration(node);
        // subtract first
        Vector2D firstVelocity = parentLayout.getVelocities().get(node);
        if (firstVelocity == null) {
            firstVelocity = new Vector2D();
        }
        parentLayout.getVelocities().put(node, firstVelocity.add(acceleration));
    }

    public Vector2D calculateAcceleration(NodeType node) {
        Vector2D centre = new Vector2D(parentLayout.getDrawingWidth() * 0.5, parentLayout.getDrawingHeight() * 0.5);
        // d = n1 - n2
        Vector2D distance = centre.subtract(node.getPosition());
        // m = |d|
        double magnitude = distance.getMagnitude();
        // v = unit(d) * force(m)
        return distance.normalize().multiply((magnitude * magnitude) / parentLayout.getForceConstant());
    }


}
