package de.bioforscher.singa.mathematics.graphs.voronoi;

import de.bioforscher.singa.mathematics.geometry.faces.Rectangle;
import de.bioforscher.singa.mathematics.graphs.model.Edge;
import de.bioforscher.singa.mathematics.graphs.model.Graph;
import de.bioforscher.singa.mathematics.graphs.model.Node;
import de.bioforscher.singa.mathematics.vectors.Vector2D;

import java.util.List;

/**
 * Just the interface for the Graph
 * implementation.
 *
 * @author cl
 */
public class VoronoiFactory<NodeType extends Node<NodeType, Vector2D, IdentifierType>,
        EdgeType extends Edge<NodeType>, IdentifierType, GraphType extends Graph<NodeType, EdgeType, IdentifierType>> {

    private double[] xValuesIn;
    private double[] yValuesIn;

    private double minX;
    private double maxX;
    private double minY;
    private double maxY;

    public VoronoiFactory() {

    }

    private void readGraph(GraphType graph) {
        xValuesIn = new double[graph.getNodes().size()];
        yValuesIn = new double[graph.getNodes().size()];
        int i = 0;
        for (Node<?, Vector2D, ?> node : graph.getNodes()) {
            Vector2D vector = node.getPosition();
            xValuesIn[i] = vector.getX();
            yValuesIn[i] = vector.getY();
            i++;
        }
    }

    private void readSpace(Rectangle rectangle) {
        minX = rectangle.getLeftMostXPosition();
        minY = rectangle.getBottomMostYPosition();
        maxX = rectangle.getRightMostXPosition();
        maxY = rectangle.getTopMostYPosition();
    }

    public List<VoronoiFaceEdge> generateVonoroi(GraphType graph, Rectangle rectangle) {
        readSpace(rectangle);
        readGraph(graph);
        Voronoi voronoi = new Voronoi(10);
        return voronoi.generateVoronoi(xValuesIn, yValuesIn, minX, maxX,
                minY, maxY);
    }

}
