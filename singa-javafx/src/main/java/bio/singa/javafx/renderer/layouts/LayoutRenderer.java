package bio.singa.javafx.renderer.layouts;

import bio.singa.mathematics.graphs.model.Edge;
import bio.singa.mathematics.graphs.model.Graph;
import bio.singa.mathematics.graphs.model.Node;
import bio.singa.mathematics.vectors.Vector2D;

public interface LayoutRenderer<NodeType extends Node<NodeType, Vector2D, IdentifierType>, EdgeType extends Edge<NodeType>,
        IdentifierType, GraphType extends Graph<NodeType, EdgeType, IdentifierType>> {


}
