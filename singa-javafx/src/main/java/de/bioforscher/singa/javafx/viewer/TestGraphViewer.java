package de.bioforscher.singa.javafx.viewer;

import de.bioforscher.singa.javafx.renderer.graphs.GraphDisplayApplication;
import de.bioforscher.singa.javafx.renderer.graphs.GraphRenderer;
import de.bioforscher.singa.mathematics.algorithms.graphs.isomorphism.RISubgraphFinder;
import de.bioforscher.singa.mathematics.graphs.model.DirectedEdge;
import de.bioforscher.singa.mathematics.graphs.model.DirectedGraph;
import de.bioforscher.singa.mathematics.graphs.model.GenericNode;
import de.bioforscher.singa.mathematics.vectors.Vector2D;
import javafx.application.Application;
import javafx.scene.paint.Color;

/**
 * @author fk
 */
public class TestGraphViewer {

    public static void main(String[] args) {


        DirectedGraph<GenericNode<String>> targetGraph = new DirectedGraph<>();
        GenericNode<String> targetNode1 = new GenericNode<>(targetGraph.nextNodeIdentifier(), "circle");
        targetGraph.addNode(targetNode1);
        GenericNode<String> targetNode2 = new GenericNode<>(targetGraph.nextNodeIdentifier(), "square");
        targetGraph.addNode(targetNode2);
        GenericNode<String> targetNode3 = new GenericNode<>(targetGraph.nextNodeIdentifier(), "triangle");
        targetGraph.addNode(targetNode3);
        targetGraph.addEdgeBetween(targetNode1, targetNode2);
        targetGraph.addEdgeBetween(targetNode2, targetNode1);
        targetGraph.addEdgeBetween(targetNode2, targetNode3);
        targetGraph.addEdgeBetween(targetNode3, targetNode2);
        targetGraph.addEdgeBetween(targetNode3, targetNode1);
        targetGraph.addEdgeBetween(targetNode1, targetNode3);


        DirectedGraph<GenericNode<String>> patternGraph = new DirectedGraph<>();
        GenericNode<String> patternNode1 = new GenericNode<>(patternGraph.nextNodeIdentifier(), "circle");
        patternGraph.addNode(patternNode1);
        GenericNode<String> patternNode2 = new GenericNode<>(patternGraph.nextNodeIdentifier(), "square");
        patternGraph.addNode(patternNode2);
        patternGraph.addEdgeBetween(patternNode1, patternNode2);
        patternGraph.addEdgeBetween(patternNode2, patternNode1);

        RISubgraphFinder<GenericNode<String>, DirectedEdge<GenericNode<String>>, Vector2D, Integer, DirectedGraph<GenericNode<String>>> finder
                = new RISubgraphFinder<>(createPatternGraph(), createTargetGraph(), (a, b) -> a.getContent().equals(b.getContent()), (a, b) -> true);
        DirectedGraph<GenericNode<GenericNode<String>>> searchSpace = finder.getSearchSpace();

        GraphDisplayApplication.graph = searchSpace;
        GraphRenderer renderer = GraphDisplayApplication.renderer;
        renderer.setRenderAfter(graph -> {
            for (GenericNode<GenericNode<String>> genericNodeGenericNode : searchSpace.getNodes()) {
                renderer.getGraphicsContext().setFill(Color.SEAGREEN);
                renderer.drawPoint(genericNodeGenericNode.getPosition(), 25);
                renderer.getGraphicsContext().setFill(Color.BLACK);

                GenericNode<String> content = genericNodeGenericNode.getContent();
                if (content != null) {
                    renderer.drawTextCenteredOnPoint(content.getContent(), genericNodeGenericNode.getPosition());
                }
            }
            return null;
        });

        Application.launch(GraphDisplayApplication.class);
    }

    private static DirectedGraph<GenericNode<String>> createPatternGraph() {
        DirectedGraph<GenericNode<String>> patternGraph = new DirectedGraph<>();
        GenericNode<String> patternNode0 = new GenericNode<>(patternGraph.nextNodeIdentifier(), "0");
        patternGraph.addNode(patternNode0);
        GenericNode<String> patternNode3 = new GenericNode<>(patternGraph.nextNodeIdentifier(), "3");
        patternGraph.addNode(patternNode3);
        GenericNode<String> patternNode4 = new GenericNode<>(patternGraph.nextNodeIdentifier(), "4");
        patternGraph.addNode(patternNode4);
        GenericNode<String> patternNode6 = new GenericNode<>(patternGraph.nextNodeIdentifier(), "6");
        patternGraph.addNode(patternNode6);
        GenericNode<String> patternNode7 = new GenericNode<>(patternGraph.nextNodeIdentifier(), "7");
        patternGraph.addNode(patternNode7);


        patternGraph.addEdgeBetween(patternNode0, patternNode3);
        patternGraph.addEdgeBetween(patternNode3, patternNode0);

        patternGraph.addEdgeBetween(patternNode3, patternNode6);
        patternGraph.addEdgeBetween(patternNode6, patternNode3);

        patternGraph.addEdgeBetween(patternNode0, patternNode4);
        patternGraph.addEdgeBetween(patternNode4, patternNode0);

        patternGraph.addEdgeBetween(patternNode4, patternNode6);
        patternGraph.addEdgeBetween(patternNode6, patternNode4);

        patternGraph.addEdgeBetween(patternNode6, patternNode7);
        patternGraph.addEdgeBetween(patternNode7, patternNode6);

        return patternGraph;
    }

    private static DirectedGraph<GenericNode<String>> createTargetGraph() {
        DirectedGraph<GenericNode<String>> targetGraph = new DirectedGraph<>();
        GenericNode<String> patternNode0 = new GenericNode<>(targetGraph.nextNodeIdentifier(), "0");
        targetGraph.addNode(patternNode0);
        GenericNode<String> patternNode1 = new GenericNode<>(targetGraph.nextNodeIdentifier(), "1");
        targetGraph.addNode(patternNode1);
        GenericNode<String> patternNode2 = new GenericNode<>(targetGraph.nextNodeIdentifier(), "2");
        targetGraph.addNode(patternNode2);
        GenericNode<String> patternNode3 = new GenericNode<>(targetGraph.nextNodeIdentifier(), "3");
        targetGraph.addNode(patternNode3);
        GenericNode<String> patternNode4 = new GenericNode<>(targetGraph.nextNodeIdentifier(), "4");
        targetGraph.addNode(patternNode4);
        GenericNode<String> patternNode5 = new GenericNode<>(targetGraph.nextNodeIdentifier(), "5");
        targetGraph.addNode(patternNode5);
        GenericNode<String> patternNode6 = new GenericNode<>(targetGraph.nextNodeIdentifier(), "6");
        targetGraph.addNode(patternNode6);
        GenericNode<String> patternNode7 = new GenericNode<>(targetGraph.nextNodeIdentifier(), "7");
        targetGraph.addNode(patternNode7);
        GenericNode<String> patternNode8 = new GenericNode<>(targetGraph.nextNodeIdentifier(), "8");
        targetGraph.addNode(patternNode8);
        GenericNode<String> patternNode9 = new GenericNode<>(targetGraph.nextNodeIdentifier(), "9");
        targetGraph.addNode(patternNode9);
        GenericNode<String> patternNode10 = new GenericNode<>(targetGraph.nextNodeIdentifier(), "10");
        targetGraph.addNode(patternNode10);


        targetGraph.addEdgeBetween(patternNode0, patternNode1);
        targetGraph.addEdgeBetween(patternNode1, patternNode0);

        targetGraph.addEdgeBetween(patternNode1, patternNode2);
        targetGraph.addEdgeBetween(patternNode2, patternNode1);

        targetGraph.addEdgeBetween(patternNode0, patternNode3);
        targetGraph.addEdgeBetween(patternNode3, patternNode0);

        targetGraph.addEdgeBetween(patternNode3, patternNode6);
        targetGraph.addEdgeBetween(patternNode6, patternNode3);

        targetGraph.addEdgeBetween(patternNode6, patternNode7);
        targetGraph.addEdgeBetween(patternNode7, patternNode6);

        targetGraph.addEdgeBetween(patternNode7, patternNode8);
        targetGraph.addEdgeBetween(patternNode8, patternNode7);

        targetGraph.addEdgeBetween(patternNode5, patternNode8);
        targetGraph.addEdgeBetween(patternNode8, patternNode5);

        targetGraph.addEdgeBetween(patternNode2, patternNode5);
        targetGraph.addEdgeBetween(patternNode5, patternNode2);

        targetGraph.addEdgeBetween(patternNode0, patternNode4);
        targetGraph.addEdgeBetween(patternNode4, patternNode0);

        targetGraph.addEdgeBetween(patternNode4, patternNode6);
        targetGraph.addEdgeBetween(patternNode6, patternNode4);

        targetGraph.addEdgeBetween(patternNode1, patternNode4);
        targetGraph.addEdgeBetween(patternNode4, patternNode1);

        targetGraph.addEdgeBetween(patternNode4, patternNode7);
        targetGraph.addEdgeBetween(patternNode7, patternNode4);

        targetGraph.addEdgeBetween(patternNode5, patternNode4);
        targetGraph.addEdgeBetween(patternNode4, patternNode5);

        targetGraph.addEdgeBetween(patternNode1, patternNode5);
        targetGraph.addEdgeBetween(patternNode5, patternNode1);

        targetGraph.addEdgeBetween(patternNode5, patternNode7);
        targetGraph.addEdgeBetween(patternNode7, patternNode5);

        targetGraph.addEdgeBetween(patternNode0, patternNode9);
        targetGraph.addEdgeBetween(patternNode9, patternNode0);

        targetGraph.addEdgeBetween(patternNode9, patternNode6);
        targetGraph.addEdgeBetween(patternNode6, patternNode9);

        targetGraph.addEdgeBetween(patternNode10, patternNode9);
        targetGraph.addEdgeBetween(patternNode9, patternNode10);
        return targetGraph;
    }
}
