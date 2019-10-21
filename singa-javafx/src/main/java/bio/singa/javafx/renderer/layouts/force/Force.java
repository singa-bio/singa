package bio.singa.javafx.renderer.layouts.force;

import bio.singa.mathematics.graphs.model.Node;
import bio.singa.mathematics.vectors.Vector2D;

/**
 *
 * @author cl
 */
public abstract class Force<NodeType extends Node<NodeType, Vector2D, ?>> {

    abstract void determineDisplacement(NodeType first, NodeType second);

}
