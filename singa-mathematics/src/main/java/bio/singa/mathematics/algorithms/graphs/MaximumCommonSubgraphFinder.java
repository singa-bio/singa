package bio.singa.mathematics.algorithms.graphs;

import bio.singa.core.utility.Pair;
import bio.singa.mathematics.graphs.model.*;
import bio.singa.mathematics.vectors.Vector;
import bio.singa.mathematics.vectors.Vector2D;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

public class MaximumCommonSubgraphFinder<NodeType extends Node<NodeType, VectorType, IdentifierType>,
        EdgeType extends Edge<NodeType>, VectorType extends Vector, IdentifierType,
        GraphType extends Graph<NodeType, EdgeType, IdentifierType>> {

    private static final Logger logger = LoggerFactory.getLogger(MaximumCommonSubgraphFinder.class);
    private final GraphType graph1;
    private final GraphType graph2;
    private List<Set<GenericNode<Pair<NodeType>>>> maximumCliques;

    public MaximumCommonSubgraphFinder(GraphType graph1, GraphType graph2) {
        this.graph1 = graph1;
        this.graph2 = graph2;
        logger.info("maximum common subgraph finder initialized with graphs of size {} and {}", graph1.getNodes().size(), graph2.getNodes().size());
        detectMaximumCliques(this.graph1, this.graph2);
    }

    private void detectMaximumCliques(GraphType graph1, GraphType graph2) {
        GenericGraph<Pair<NodeType>> modularProduct = Graphs.modularProduct(graph1, graph2);

        // detect maximum cliques
        BronKerbosch<GenericNode<Pair<NodeType>>, GenericEdge<Pair<NodeType>>, Vector2D, Integer, GenericGraph<Pair<NodeType>>> bronKerbosch = new BronKerbosch<>(modularProduct);
        maximumCliques = bronKerbosch.getMaximumCliques();
    }

    public List<GraphType> getMaximumCliquesAsGraphs() {
        if (maximumCliques.isEmpty()) {
            throw new NoSuchElementException("no maximum cliques found betwene graphs " + graph1 + " and " + graph2);
        } else {
            ArrayList<GraphType> cliqueGraphs = new ArrayList<>();
            for (Set<GenericNode<Pair<NodeType>>> maximumClique : maximumCliques) {
                GraphType cliqueGraph = (GraphType) graph1.getCopy();
                Set<IdentifierType> cliqueIdentifiers = maximumClique.stream()
                        .map(GenericNode::getContent)
                        .map(Pair::getFirst)
                        .map(NodeType::getIdentifier)
                        .collect(Collectors.toSet());
                Set<NodeType> nodesToDelete = cliqueGraph.getNodes().stream()
                        .filter(node -> !cliqueIdentifiers.contains(node.getIdentifier()))
                        .collect(Collectors.toSet());
                nodesToDelete.forEach(cliqueGraph::removeNode);
                cliqueGraphs.add(cliqueGraph);
            }
            return cliqueGraphs;
        }
    }
}
