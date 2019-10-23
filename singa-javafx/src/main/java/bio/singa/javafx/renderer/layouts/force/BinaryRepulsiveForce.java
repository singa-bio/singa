package bio.singa.javafx.renderer.layouts.force;

import bio.singa.mathematics.graphs.model.Edge;
import bio.singa.mathematics.graphs.model.Graph;
import bio.singa.mathematics.graphs.model.Node;
import bio.singa.mathematics.vectors.Vector2D;

/**
 * @author cl
 */
public class BinaryRepulsiveForce<NodeType extends Node<NodeType, Vector2D, IdentifierType>, EdgeType extends Edge<NodeType>,
        IdentifierType, GraphType extends Graph<NodeType, EdgeType, IdentifierType>> extends BinaryForce<NodeType, EdgeType, IdentifierType, GraphType> {

    public BinaryRepulsiveForce(ForceDirectedGraphLayout<NodeType, EdgeType, IdentifierType, GraphType> parentLayout) {
        super(parentLayout);
        setForcePredicate((first, second) -> true);
    }

    public void determineDisplacement(NodeType first, NodeType second) {
        Vector2D acceleration = calculateAcceleration(first, second);
        // add first
        Vector2D firstVelocity = getParentLayout().getVelocities().get(first);
        if (firstVelocity == null) {
            firstVelocity = Vector2D.ZERO;
        }
        getParentLayout().getVelocities().put(first, firstVelocity.add(acceleration));
        // subtract second
        Vector2D secondVelocity = getParentLayout().getVelocities().get(second);
        if (secondVelocity == null) {
            secondVelocity = Vector2D.ZERO;
        }
        getParentLayout().getVelocities().put(second, secondVelocity.subtract(acceleration));
    }

    public Vector2D calculateAcceleration(NodeType first, NodeType second) {
        // d = n1 - n2
        Vector2D distance = first.getPosition().subtract(second.getPosition());
        // m = |d|
        double magnitude = distance.getMagnitude();
        // v = unit(d) * force(m)
        return distance.normalize()
                .multiply((getForceConstant().doubleValue() * getForceConstant().doubleValue()) / magnitude)
                .multiply(getForceMultiplier());
    }

}
