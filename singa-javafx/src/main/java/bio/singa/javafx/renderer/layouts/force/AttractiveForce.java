package bio.singa.javafx.renderer.layouts.force;

import bio.singa.mathematics.graphs.model.Node;
import bio.singa.mathematics.vectors.Vector2D;

import java.util.function.BiPredicate;

/**
 * @author cl
 */
public class AttractiveForce<NodeType extends Node<NodeType, Vector2D, ?>> extends Force<NodeType> {

    private ForceDirectedGraphLayout<NodeType, ?, ?, ?> parentLayout;
    private BiPredicate<NodeType, NodeType> forcePredicate;

    public AttractiveForce(ForceDirectedGraphLayout<NodeType, ?, ?, ?> parentLayout) {
        this.parentLayout = parentLayout;
        forcePredicate = (first, second) -> first.getNeighbours().contains(second);
    }

    public BiPredicate<NodeType, NodeType> getForcePredicate() {
        return forcePredicate;
    }

    public void setForcePredicate(BiPredicate<NodeType, NodeType> forcePredicate) {
        this.forcePredicate = forcePredicate;
    }

    public void determineDisplacement(NodeType first, NodeType second) {
        if (!forcePredicate.test(first, second)) {
            return;
        }
        Vector2D acceleration = calculateAcceleration(first, second);
        // subtract first
        Vector2D firstVelocity = parentLayout.getVelocities().get(first);
        if (firstVelocity == null) {
            firstVelocity = new Vector2D();
        }
        parentLayout.getVelocities().put(first, firstVelocity.subtract(acceleration));
        // add second
        Vector2D secondVelocity = parentLayout.getVelocities().get(second);
        if (secondVelocity == null) {
            secondVelocity = new Vector2D();
        }
        parentLayout.getVelocities().put(second, secondVelocity.add(acceleration));
    }

    public Vector2D calculateAcceleration(NodeType first, NodeType second) {
        // d = n1 - n2
        Vector2D distance = first.getPosition().subtract(second.getPosition());
        // m = |d|
        double magnitude = distance.getMagnitude();
        // v = unit(d) * force(m)
        return distance.normalize().multiply((magnitude * magnitude) / parentLayout.getForceConstant());
    }

}
