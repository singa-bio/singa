package bio.singa.javafx.renderer.graphs;

import bio.singa.mathematics.graphs.model.Node;
import bio.singa.mathematics.vectors.Vector2D;

import java.util.Map;
import java.util.function.BiFunction;

/**
 * @author cl
 */
public class RepulsiveForce<NodeType extends Node<NodeType, Vector2D, ?>> extends Force<NodeType> {

    private Map<NodeType, Vector2D> velocities;

    public RepulsiveForce(BiFunction<NodeType, NodeType, Vector2D> force, Map<NodeType, Vector2D> velocities) {
        super(force);
        this.velocities = velocities;
    }

    public void calculateAcceleration(NodeType first, NodeType second) {
        Vector2D acceleration = getForce().apply(first, second);
        Vector2D firstVelocity = velocities.get(first);
        if (firstVelocity == null) {
            firstVelocity = new Vector2D();
        }
        velocities.put(first, firstVelocity.add(acceleration));

        Vector2D secondVelocity = velocities.get(second);
        if (secondVelocity == null) {
            secondVelocity = new Vector2D();
        }
        velocities.put(second, secondVelocity.subtract(acceleration));
    }


}
