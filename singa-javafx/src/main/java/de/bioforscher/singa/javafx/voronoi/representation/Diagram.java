package de.bioforscher.singa.javafx.voronoi.representation;

import de.bioforscher.singa.mathematics.vectors.Vector2D;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Christoph on 20/05/2017.
 */
public class Diagram {

    private Map<Vector2D, Cell> cells;
    private List<Edge> edges;
    private List<Vector2D> vertices;

    public Diagram() {
        this.cells = new HashMap<>();
        this.edges = new ArrayList<>();
        this.vertices = new ArrayList<>();
    }

    public List<Edge> getEdges() {
        return this.edges;
    }

    public void setEdges(List<Edge> edges) {
        this.edges = edges;
    }

    public Edge createEdge(Vector2D lSite, Vector2D rSite, Vector2D va, Vector2D vb) {
        Edge edge = new Edge(lSite, rSite);
        this.edges.add(edge);
        if (va != null) {
            edge.setEdgeStartPoint(lSite, rSite, va);
        }
        if (vb != null) {
            edge.setEdgeEndPoint(lSite, rSite, vb);
        }
        this.cells.get(lSite).getHalfEdges().add(new HalfEdge(edge, lSite, rSite));
        this.cells.get(rSite).getHalfEdges().add(new HalfEdge(edge, rSite, lSite));
        return edge;
    }

    public Edge createBorderEdge(Vector2D lSite, Vector2D va, Vector2D vb) {
        Edge edge = new Edge(lSite, null);
        edge.setVa(va);
        edge.setVb(vb);
        this.edges.add(edge);
        return edge;
    }

    public List<Vector2D> getVertices() {
        return this.vertices;
    }

    public void setVertices(List<Vector2D> vertices) {
        this.vertices = vertices;
    }

    public Vector2D createVertex(double x, double y) {
        Vector2D vertex = new Vector2D(x,y);
        this.vertices.add(vertex);
        return vertex;
    }

    // TODO continue with diagram completion methods

}
