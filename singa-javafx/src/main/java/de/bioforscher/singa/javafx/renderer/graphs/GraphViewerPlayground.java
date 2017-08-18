package de.bioforscher.singa.javafx.renderer.graphs;

import de.bioforscher.singa.mathematics.algorithms.geometry.ConvexHull;
import de.bioforscher.singa.mathematics.algorithms.graphs.DisconnectedSubgraphFinder;
import de.bioforscher.singa.mathematics.geometry.faces.Rectangle;
import de.bioforscher.singa.mathematics.graphs.model.*;
import de.bioforscher.singa.mathematics.vectors.Vector2D;
import javafx.application.Application;
import javafx.scene.paint.Color;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author fk
 */
public class GraphViewerPlayground {

    public static void main(String[] args) {


        GraphDisplayApplication.graph = Graphs.buildRandomGraph(100, 0.005, new Rectangle(400, 400));
        GraphRenderer<RegularNode, UndirectedEdge, Integer, UndirectedGraph> renderer = new GraphRenderer<>();
        GraphDisplayApplication.renderer = renderer;

        renderer.setRenderBefore((currentGraph) -> {
            renderer.getGraphicsContext().setStroke(Color.CADETBLUE);
            List<UndirectedGraph> disconnectedSubgraphs = DisconnectedSubgraphFinder.findDisconnectedSubgraphs(currentGraph);
            disconnectedSubgraphs.forEach(graph -> {
                List<Vector2D> hull = ConvexHull.calculateHullFor(graph.getNodes().stream()
                        .map(Node::getPosition)
                        .collect(Collectors.toList())).getHull();
                renderer.connectPoints(hull);
            });
            return null;
        });

        Application.launch(GraphDisplayApplication.class);
    }


}
