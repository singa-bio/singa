package bio.singa.javafx.renderer.layouts.force;

import bio.singa.mathematics.graphs.model.Node;
import bio.singa.mathematics.vectors.Vector2D;
import javafx.beans.binding.DoubleBinding;

/**
 * @author cl
 */
public class UnaryAttractiveForce<NodeType extends Node<NodeType, Vector2D, ?>> extends UnaryForce<NodeType> {

    private final DoubleBinding referenceX;
    private final DoubleBinding referenceY;

    public UnaryAttractiveForce(ForceDirectedGraphLayout<NodeType, ?, ?, ?> parentLayout, DoubleBinding referenceX, DoubleBinding referenceY) {
        super(parentLayout);
        this.referenceX = referenceX;
        this.referenceY = referenceY;
        setForcePredicate(node -> true);
    }

    public void determineDisplacement(NodeType node) {
        Vector2D acceleration = calculateAcceleration(node);
        // add
        Vector2D velocity = getParentLayout().getVelocities().get(node);
        if (velocity == null) {
            velocity = new Vector2D();
        }
        getParentLayout().getVelocities().put(node, velocity.add(acceleration));
    }

    public Vector2D calculateAcceleration(NodeType node) {
        Vector2D centre = new Vector2D(getParentLayout().getDrawingWidth()/2, getParentLayout().getDrawingHeight()/2);
        // d = n1 - n2
        Vector2D distance = centre.subtract(node.getPosition());
        // m = |d|
        double magnitude = distance.getMagnitude();
        // v = unit(d) * force(m)
        return distance.normalize().multiply((magnitude * magnitude) / getForceConstant().doubleValue());
    }


}
