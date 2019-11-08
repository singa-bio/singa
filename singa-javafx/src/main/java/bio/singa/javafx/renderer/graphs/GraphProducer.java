package bio.singa.javafx.renderer.graphs;

import bio.singa.javafx.renderer.layouts.force.ForceDirectedGraphLayout;
import bio.singa.mathematics.graphs.model.Edge;
import bio.singa.mathematics.graphs.model.Graph;
import bio.singa.mathematics.graphs.model.Node;
import bio.singa.mathematics.vectors.Vector2D;

import java.util.Collection;

/**
 * @author cl
 */
public class GraphProducer<NodeType extends Node<NodeType, Vector2D, IdentifierType>, EdgeType extends Edge<NodeType>,
        IdentifierType, GraphType extends Graph<NodeType, EdgeType, IdentifierType>> implements Runnable {

    private ForceDirectedGraphLayout<NodeType, EdgeType, IdentifierType, GraphType> layout;
    private final GraphRenderer<NodeType, EdgeType, IdentifierType, GraphType> renderer;
    private Collection<IdentifierType> fixedIdentifiers;

    public GraphProducer(ForceDirectedGraphLayout<NodeType, EdgeType, IdentifierType, GraphType> layout, GraphRenderer<NodeType, EdgeType, IdentifierType, GraphType> renderer) {
        this.renderer = renderer;
        this.layout = layout;
    }

    public Collection<IdentifierType> getFixedIdentifiers() {
        return fixedIdentifiers;
    }

    public void setFixedIdentifiers(Collection<IdentifierType> fixedIdentifiers) {
        this.fixedIdentifiers = fixedIdentifiers;
    }

    @Override
    public void run() {
        if (fixedIdentifiers != null) {
            layout.fixNodes(fixedIdentifiers);
        }
        for (int i = 0; i < 100; i++) {
            renderer.getGraphQueue().add(layout.arrangeGraph(i));
            try {
                Thread.sleep(40);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
