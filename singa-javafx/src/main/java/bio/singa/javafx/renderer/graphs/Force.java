package bio.singa.javafx.renderer.graphs;

import bio.singa.mathematics.graphs.model.Node;
import bio.singa.mathematics.vectors.Vector2D;

import java.util.function.BiFunction;

/**
 *
 * @author cl
 */
public interface Force<NodeType extends Node<NodeType, Vector2D, ?>> extends BiFunction<NodeType, NodeType, Vector2D> {

}
