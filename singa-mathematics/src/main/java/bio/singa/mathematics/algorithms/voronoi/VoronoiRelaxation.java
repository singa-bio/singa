package bio.singa.mathematics.algorithms.voronoi;

import bio.singa.mathematics.algorithms.voronoi.model.VoronoiCell;
import bio.singa.mathematics.algorithms.voronoi.model.VoronoiDiagram;
import bio.singa.mathematics.geometry.faces.Rectangle;
import bio.singa.mathematics.graphs.model.Edge;
import bio.singa.mathematics.graphs.model.Graph;
import bio.singa.mathematics.graphs.model.Node;
import bio.singa.mathematics.vectors.Vector2D;

import java.util.*;

/**
 * Lloyd's algorithm, also known as Voronoi iteration or relaxation, is an algorithm named after Stuart P. Lloyd for
 * finding evenly spaced sets of points in subsets of Euclidean spaces and partitions of these subsets into well-shaped
 * and uniformly sized convex cells.
 * <pre>
 * Lloyd, Stuart. "Least squares quantization in PCM."
 * IEEE transactions on information theory 28.2 (1982): 129-137.
 * </pre>
 *
 * @author cl
 * @see <a href="http://ieeexplore.ieee.org/document/1056489/">Least squares quantization in PCM.</a>
 */
public class VoronoiRelaxation {

    private VoronoiRelaxation() {
    }

    /**
     * Computes one iteration of lloyd's relaxation. Creates a new relaxed diagram.
     *
     * @param diagram The Voronoi diagram before relaxation.
     * @return The Voronoi diagram after relaxation.
     */
    public static List<Vector2D> relax(VoronoiDiagram diagram) {
        final Collection<VoronoiCell> cells = diagram.getCells();
        List<Vector2D> updatedSites = new ArrayList<>();
        for (VoronoiCell cell : cells) {
            // distance between centroid and actual site position
            final Vector2D centroid = cell.getCentroid();
            final Vector2D site = cell.getSite().getSite();
            final double distance = site.distanceTo(centroid);
            // set next position to midpoint
            if (distance > 2.0) {
                updatedSites.add(centroid.getMidpointTo(site));
            } else {
                updatedSites.add(centroid);
            }
        }
        return updatedSites;
    }


    public static <NodeType extends Node<NodeType, Vector2D, IdentifierType>,
            EdgeType extends Edge<NodeType>, IdentifierType,
            GraphType extends Graph<NodeType, EdgeType, IdentifierType>> GraphType relax(GraphType graph, Rectangle boundingBox) {

        HashMap<Integer, NodeType> nodeMap = new HashMap<>();
        int identifier = 0;
        for (NodeType nodeType : graph.getNodes()) {
            nodeMap.put(identifier, nodeType);
            identifier++;
        }

        final VoronoiDiagram diagram = VoronoiGenerator.generateVoronoiDiagram(nodeMap, boundingBox);
        final Collection<VoronoiCell> cells = diagram.getCells();
        Map<Integer, Vector2D> updatedSites = new HashMap<>();
        for (VoronoiCell cell : cells) {
            // distance between centroid and actual site positionQQ
            final Vector2D centroid = cell.getCentroid();
            final Vector2D site = cell.getSite().getSite();
            final double distance = site.distanceTo(centroid);
            updatedSites.put(cell.getSite().getIdentifier(), centroid.getMidpointTo(site));
        }

        for (Map.Entry<Integer, NodeType> mappingEntry : nodeMap.entrySet()) {
            mappingEntry.getValue().setPosition(updatedSites.get(mappingEntry.getKey()));
        }

        return graph;
    }

}
