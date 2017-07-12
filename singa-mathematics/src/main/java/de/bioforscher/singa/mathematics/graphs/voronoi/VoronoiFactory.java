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
public class VoronoiFactory <NodeType extends Node<NodeType, Vector2D>,
        EdgeType extends Edge<NodeType>, GraphType extends Graph<NodeType, EdgeType>> {

    private double[] xValuesIn;
    private double[] yValuesIn;

    private double minX;
    private double maxX;
    private double minY;
    private double maxY;

    public VoronoiFactory() {

    }

    private void readGraph(GraphType graph) {
        this.xValuesIn = new double[graph.getNodes().size()];
        this.yValuesIn = new double[graph.getNodes().size()];
        int i = 0;
        for (Node<?, Vector2D> node : graph.getNodes()) {
            Vector2D vector = node.getPosition();
            this.xValuesIn[i] = vector.getX();
            this.yValuesIn[i] = vector.getY();
            i++;
        }
    }

    private void readSpace(Rectangle rectangle) {
        this.minX = rectangle.getLeftMostXPosition();
        this.minY = rectangle.getBottomMostYPosition();
        this.maxX = rectangle.getRightMostXPosition();
        this.maxY = rectangle.getTopMostYPosition();
    }

    public List<VoronoiFaceEdge> generateVonoroi(GraphType graph, Rectangle rectangle) {
        this.readSpace(rectangle);
        this.readGraph(graph);
        Voronoi voronoi = new Voronoi(10);
        return voronoi.generateVoronoi(this.xValuesIn, this.yValuesIn, this.minX, this.maxX,
                this.minY, this.maxY);
    }

}
