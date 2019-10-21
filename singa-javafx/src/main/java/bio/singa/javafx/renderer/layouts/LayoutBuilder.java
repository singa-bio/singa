package bio.singa.javafx.renderer.layouts;

import bio.singa.javafx.renderer.layouts.force.Force;
import bio.singa.javafx.renderer.layouts.force.ForceDirectedGraphLayout;
import bio.singa.mathematics.graphs.model.Edge;
import bio.singa.mathematics.graphs.model.Graph;
import bio.singa.mathematics.graphs.model.Node;
import bio.singa.mathematics.vectors.Vector2D;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

import java.util.Arrays;
import java.util.Collection;

public class LayoutBuilder {

    public static <NodeType extends Node<NodeType, Vector2D, IdentifierType>, EdgeType extends Edge<NodeType>,
            IdentifierType, GraphType extends Graph<NodeType, EdgeType, IdentifierType>> ForceStep<NodeType, EdgeType, IdentifierType, GraphType> forceDirected(GraphType graph) {
        return new ForceDirectedLayoutBuilder<>(graph);
    }

    public interface GraphStep<NodeType extends Node<NodeType, Vector2D, IdentifierType>, EdgeType extends Edge<NodeType>,
            IdentifierType, GraphType extends Graph<NodeType, EdgeType, IdentifierType>> {

        ForceStep<NodeType, EdgeType, IdentifierType, GraphType> graph(GraphType graph);

    }

    public interface ForceStep<NodeType extends Node<NodeType, Vector2D, IdentifierType>, EdgeType extends Edge<NodeType>,
            IdentifierType, GraphType extends Graph<NodeType, EdgeType, IdentifierType>> {

        OptionsStep<NodeType, EdgeType, IdentifierType, GraphType> defaultForces();

        OptionsStep<NodeType, EdgeType, IdentifierType, GraphType> customForces(Force<NodeType> attractiveForce, Force<NodeType> repulsiveForce);

    }

    public interface OptionsStep<NodeType extends Node<NodeType, Vector2D, IdentifierType>, EdgeType extends Edge<NodeType>,
            IdentifierType, GraphType extends Graph<NodeType, EdgeType, IdentifierType>> {

        OptionsStep fixNodes(Collection<IdentifierType> identifiers);

        OptionsStep fixNodes(IdentifierType... identifiers);

        OptionsStep canvasSize(DoubleProperty canvasWidth, DoubleProperty canvasHeight);

        OptionsStep canvasSize(double canvasWidth, double canvasHeight);

        LayoutRenderer build();

    }


    public static class ForceDirectedLayoutBuilder<NodeType extends Node<NodeType, Vector2D, IdentifierType>, EdgeType extends Edge<NodeType>,
            IdentifierType, GraphType extends Graph<NodeType, EdgeType, IdentifierType>> implements ForceStep<NodeType, EdgeType, IdentifierType, GraphType>, OptionsStep<NodeType, EdgeType, IdentifierType, GraphType> {

        private GraphType graph;
        private boolean defaultForces;
        private Force<NodeType> attractiveForce;
        private Force<NodeType> repulsiveForce;


        private Collection<IdentifierType> fixedNodes;
        private DoubleProperty canvasWidth;
        private DoubleProperty canvasHeight;

        public ForceDirectedLayoutBuilder(GraphType graph) {
            this.graph = graph;
        }

        @Override
        public OptionsStep<NodeType, EdgeType, IdentifierType, GraphType> defaultForces() {
            defaultForces = true;
            return this;
        }

        @Override
        public OptionsStep<NodeType, EdgeType, IdentifierType, GraphType> customForces(Force<NodeType> attractiveForce, Force<NodeType> repulsiveForce) {
            this.attractiveForce = attractiveForce;
            this.repulsiveForce = repulsiveForce;
            return this;
        }

        @Override
        public OptionsStep canvasSize(DoubleProperty canvasWidth, DoubleProperty canvasHeight) {
            this.canvasWidth = canvasWidth;
            this.canvasHeight = canvasHeight;
            return this;
        }

        @Override
        public OptionsStep canvasSize(double canvasWidth, double canvasHeight) {
            return canvasSize(new SimpleDoubleProperty(canvasWidth), new SimpleDoubleProperty(canvasHeight));
        }

        @Override
        public OptionsStep fixNodes(Collection<IdentifierType> identifiers) {
            fixedNodes = identifiers;
            return this;
        }

        @Override
        public OptionsStep fixNodes(IdentifierType... identifiers) {
            return fixNodes(Arrays.asList(identifiers));
        }

        @Override
        public LayoutRenderer build() {
            if (canvasHeight == null) {
                canvasHeight = new SimpleDoubleProperty(500);
            }
            if (canvasWidth == null) {
                canvasWidth = new SimpleDoubleProperty(500);
            }
            return new ForceDirectedGraphLayout<>(graph, canvasWidth, canvasHeight, 100);
        }
    }

}
