package bio.singa.javafx.renderer.layouts.force;

import bio.singa.mathematics.graphs.model.Edge;
import bio.singa.mathematics.graphs.model.Graph;
import bio.singa.mathematics.graphs.model.Node;
import bio.singa.mathematics.vectors.Vector2D;
import javafx.beans.binding.DoubleBinding;

/**
 * @author cl
 */
public class UnaryAttractiveForce<NodeType extends Node<NodeType, Vector2D, IdentifierType>, EdgeType extends Edge<NodeType>,
        IdentifierType, GraphType extends Graph<NodeType, EdgeType, IdentifierType>> extends UnaryForce<NodeType, EdgeType, IdentifierType, GraphType> {

    private final DoubleBinding referenceX;
    private final DoubleBinding referenceY;

    public UnaryAttractiveForce(ForceDirectedGraphLayout<NodeType, EdgeType, IdentifierType, GraphType> parentLayout, DoubleBinding referenceX, DoubleBinding referenceY) {
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
            velocity = Vector2D.ZERO;
        }
        getParentLayout().getVelocities().put(node, velocity.add(acceleration));
    }

    public Vector2D calculateAcceleration(NodeType node) {
        Vector2D centre = new Vector2D(referenceX.get(), referenceY.get());
        // d = n1 - n2
        Vector2D distance = centre.subtract(node.getPosition());
        // m = |d|
        double magnitude = distance.getMagnitude();
        // v = unit(d) * force(m)
        return distance.normalize()
                .multiply((magnitude * magnitude) / getForceConstant().doubleValue())
                .multiply(getForceMultiplier());
    }


}
