package bio.singa.mathematics.algorithms.graphs;

import bio.singa.mathematics.graphs.model.Edge;
import bio.singa.mathematics.graphs.model.Graph;
import bio.singa.mathematics.graphs.model.Node;
import bio.singa.mathematics.vectors.Vector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * An implementation of the Bron-Kerbosch algorithm for clique detection in undirected graphs. Finds all cliques and has
 * a convenience method to return the maximum clique (or multiple if of same size).
 * <b>Note: Not tested for directed graphs.</b>
 *
 * @param <NodeType> The type of the nodes.
 * @param <EdgeType> The type of the edges.
 * @param <VectorType> The position type.
 * @param <IdentifierType> The type of the identifier.
 * @param <GraphType> The type of the graph.
 */
public class BronKerbosch<NodeType extends Node<NodeType, VectorType, IdentifierType>,
        EdgeType extends Edge<NodeType>, VectorType extends Vector, IdentifierType,
        GraphType extends Graph<NodeType, EdgeType, IdentifierType>> {

    private static final Logger logger = LoggerFactory.getLogger(BronKerbosch.class);
    private final GraphType graph;
    private List<Set<NodeType>> cliques;


    public BronKerbosch(GraphType graph) {
        this.graph = graph;
        cliques = new ArrayList<>();
        findCliques();
    }

    public List<Set<NodeType>> getCliques() {
        return cliques;
    }

    private void findCliques() {
        bronKerbosch(new HashSet<>(), new HashSet<>(graph.getNodes()), new HashSet<>());
        logger.info("found {} maximum cliques", cliques.size());
    }

    /**
     * @return The maximum cliques (at least one clique).
     */
    public List<Set<NodeType>> getMaximumCliques() {
        int maximumCliqueSize = 0;
        List<Set<NodeType>> maximumCliques = new ArrayList<>();
        for (Set<NodeType> clique : cliques) {
            if (clique.size() >= maximumCliqueSize) {
                if (clique.size() > maximumCliqueSize) {
                    maximumCliques.clear();
                }
                maximumCliques.add(clique);
                maximumCliqueSize = clique.size();
            }
        }
        return maximumCliques;
    }

    private Set<NodeType> bronKerbosch(Set<NodeType> r, Set<NodeType> p, Set<NodeType> x) {

        if (p.isEmpty() && x.isEmpty()) {
            cliques.add(r);
            return r;
        }

        Set<NodeType> pUnionx = new HashSet<>();
        pUnionx.addAll(p);
        pUnionx.addAll(x);

        // choose pivot node with high degree
        NodeType pivotNode = pUnionx.stream()
                .max(Comparator.comparingInt(NodeType::getDegree))
                .orElseThrow(NoSuchElementException::new);

        Set<NodeType> pWithoutPivotNeighbors = new HashSet<>(p);
        List<NodeType> pivotNeighbours = pivotNode.getNeighbours();
        pWithoutPivotNeighbors.removeAll(pivotNeighbours);
        for (NodeType v : pWithoutPivotNeighbors) {
            Set<NodeType> rUnionV = new HashSet<>(r);
            rUnionV.add(v);
            Set<NodeType> pIntersectionNeighbors = new HashSet<>(p);
            pIntersectionNeighbors.retainAll(v.getNeighbours());
            Set<NodeType> xIntersectionNeighbors = new HashSet<>(x);
            xIntersectionNeighbors.retainAll(v.getNeighbours());
            bronKerbosch(rUnionV, pIntersectionNeighbors, xIntersectionNeighbors);
            Set<NodeType> pWithoutV = new HashSet<>(p);
            pWithoutV.remove(v);
            p = pWithoutV;
            Set<NodeType> xUnionv = new HashSet<>(x);
            xUnionv.add(v);
            x = xUnionv;
        }
        return null;
    }
}
