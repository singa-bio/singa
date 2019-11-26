package bio.singa.mathematics.algorithms.graphs;

import bio.singa.core.utility.Pair;
import bio.singa.mathematics.graphs.model.*;
import bio.singa.mathematics.vectors.Vector;
import bio.singa.mathematics.vectors.Vector2D;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;

public class MaximumCommonSubgraphFinder<NodeType extends Node<NodeType, VectorType, IdentifierType>,
        EdgeType extends Edge<NodeType>, VectorType extends Vector, IdentifierType,
        GraphType extends Graph<NodeType, EdgeType, IdentifierType>> {

    private static final Logger logger = LoggerFactory.getLogger(MaximumCommonSubgraphFinder.class);

    public MaximumCommonSubgraphFinder(GraphType graph1, GraphType graph2) {
        logger.info("maximum common subgraph finder initialized with graphs of size {} and {}", graph1.getNodes().size(), graph2.getNodes().size());
        GenericGraph<Pair<NodeType>> modularProduct = Graphs.modularProduct(graph1, graph2);

        // detect maximum cliques
        BronKerbosch<GenericNode<Pair<NodeType>>, GenericEdge<Pair<NodeType>>, Vector2D, Integer, GenericGraph<Pair<NodeType>>> bronKerbosch = new BronKerbosch<>(modularProduct);
        List<Set<GenericNode<Pair<NodeType>>>> maximumCliques = bronKerbosch.getMaximumCliques();
        System.out.println();
    }
}
