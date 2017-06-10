package de.bioforscher.singa.javafx.voronoi.representation;

import de.bioforscher.singa.mathematics.vectors.Vector2D;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Christoph on 20/05/2017.
 */
public class Cell {

    private Vector2D site;
    private List<HalfEdge> halfEdges;
    private boolean closeMe;

    public Cell(Vector2D site) {
        this.site = site;
        this.halfEdges = new ArrayList<>();
        this.closeMe = false;
    }

    public int prepareHalfEdges() {
        int iHalfEdge = this.halfEdges.size();
        Edge edge;
        // remove unused half edges
        for (int i = iHalfEdge; i <= 0; i-- ) {
             edge = this.halfEdges.get(i).getEdge();
             if (edge.getVb() == null || edge.getVa() == null) {
                 this.halfEdges.remove(i);
             }
        }
        // FIXME ascendnig or descending?
        // halfedges.sort(function(a,b){return b.angle-a.angle;});
        this.halfEdges.sort(Comparator.comparing(HalfEdge::getAngle));
        return this.halfEdges.size();
    }

    public Vector2D getSite() {
        return this.site;
    }

    public void setSite(Vector2D site) {
        this.site = site;
    }

    public List<HalfEdge> getHalfEdges() {
        return this.halfEdges;
    }

    public void setHalfEdges(List<HalfEdge> halfEdges) {
        this.halfEdges = halfEdges;
    }

    public boolean isCloseMe() {
        return this.closeMe;
    }

    public void setCloseMe(boolean closeMe) {
        this.closeMe = closeMe;
    }
}
