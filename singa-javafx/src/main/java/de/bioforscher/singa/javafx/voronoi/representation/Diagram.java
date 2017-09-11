package de.bioforscher.singa.javafx.voronoi.representation;

import de.bioforscher.singa.javafx.voronoi.Site;
import de.bioforscher.singa.mathematics.vectors.Vector2D;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Christoph on 20/05/2017.
 */
public class Diagram {

    private Map<Integer, Cell> cells;
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

    public Edge createEdge(Site lSite, Site rSite, Site va, Site vb) {
        Edge edge = new Edge(lSite, rSite);
        this.edges.add(edge);
        if (va != null) {
            edge.setEdgeStartPoint(lSite, rSite, va);
        }
        if (vb != null) {
            edge.setEdgeEndPoint(lSite, rSite, vb);
        }
        this.cells.get(lSite.getVoronoiId()).getHalfEdges().add(new HalfEdge(edge, lSite, rSite));
        this.cells.get(rSite.getVoronoiId()).getHalfEdges().add(new HalfEdge(edge, rSite, lSite));
        return edge;
    }

    public Edge createEdge(Site lSite, Site rSite) {
        return createEdge(lSite, rSite, null, null);
    }

    public Edge createBorderEdge(Site lSite, Site va, Site vb) {
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

    public Cell createCell(int siteId, Site site) {
        site.setVoronoiId(siteId);
        Cell cell = new Cell(site);
        this.cells.put(siteId, cell);
        return cell;
    }


    public void clipEdges() {}

    // TODO continue with diagram completion methods

}
