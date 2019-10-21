package bio.singa.javafx.renderer.graphs;

import bio.singa.mathematics.graphs.model.Node;
import bio.singa.mathematics.vectors.Vector2D;

import java.util.function.BiFunction;

/**
 *
 * @author cl
 */
public abstract class Force<NodeType extends Node<NodeType, Vector2D, ?>> {

    private BiFunction<NodeType, NodeType, Vector2D> force;

    public Force(BiFunction<NodeType, NodeType, Vector2D> force) {
        this.force = force;
    }

    public BiFunction<NodeType, NodeType, Vector2D> getForce() {
        return force;
    }

    public void setForce(BiFunction<NodeType, NodeType, Vector2D> force) {
        this.force = force;
    }

    public abstract void calculateAcceleration(NodeType first, NodeType second);

}
