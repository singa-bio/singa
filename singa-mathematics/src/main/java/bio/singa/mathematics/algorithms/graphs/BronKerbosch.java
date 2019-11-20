package bio.singa.mathematics.algorithms.graphs;

import bio.singa.mathematics.graphs.model.Edge;
import bio.singa.mathematics.graphs.model.Graph;
import bio.singa.mathematics.graphs.model.Node;
import bio.singa.mathematics.vectors.Vector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class BronKerbosch<NodeType extends Node<NodeType, VectorType, IdentifierType>,
        EdgeType extends Edge<NodeType>, VectorType extends Vector, IdentifierType> {

    private static final Logger logger = LoggerFactory.getLogger(BronKerbosch.class);
    private final Graph<NodeType, EdgeType, IdentifierType> graph;
    private List<Set<NodeType>> cliques;


    public BronKerbosch(Graph<NodeType, EdgeType, IdentifierType> graph) {
        this.graph = graph;
        cliques = new ArrayList<>();
    }

    public List<Set<NodeType>> findCliques() {
        bronKerbosch(new HashSet<>(), new HashSet<>(graph.getNodes()), new HashSet<>());
        logger.info("found {} maximum cliques", cliques.size());
        return cliques;
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
            Set<NodeType> maximalClique = bronKerbosch(rUnionV, pIntersectionNeighbors, xIntersectionNeighbors);
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
