package bio.singa.chemistry.entities.graphcomplex;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.core.utility.Pair;
import bio.singa.mathematics.graphs.model.AbstractMapGraph;
import bio.singa.mathematics.graphs.model.Graphs;
import bio.singa.mathematics.graphs.model.Node;
import bio.singa.mathematics.vectors.Vector2D;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author cl
 */
public class GraphComplex extends AbstractMapGraph<GraphComplexNode, GraphComplexEdge, Vector2D, Integer> {

    private static final Logger logger = LoggerFactory.getLogger(GraphComplex.class);

    static GraphComplex from(ChemicalEntity chemicalEntity) {
        GraphComplex graph = new GraphComplex();
        GraphComplexNode node = new GraphComplexNode(graph.nextNodeIdentifier());
        node.setEntity(chemicalEntity);
        graph.addNode(node);
        return graph;
    }

    static GraphComplex from(ChemicalEntity chemicalEntity, BindingSite bindingSite) {
        GraphComplex graph = from(chemicalEntity);
        graph.addBindingSite(chemicalEntity, bindingSite);
        return graph;
    }

    public GraphComplex() {
    }

    private GraphComplex(GraphComplex graphComplex, int nodeOffset, int edgeOffset) {
        for (GraphComplexNode node : graphComplex.getNodes()) {
            GraphComplexNode copy = node.getCopy(nodeOffset);
            addNode(copy);
        }
        for (GraphComplexEdge edge : graphComplex.getEdges()) {
            GraphComplexEdge edgeCopy = edge.getCopy(edgeOffset);
            GraphComplexNode source = getNode(edge.getSource().getIdentifier() + nodeOffset);
            GraphComplexNode target = getNode(edge.getTarget().getIdentifier() + nodeOffset);
            addEdgeBetween(edgeCopy, source, target);
        }
    }

    @Override
    public int addEdgeBetween(int identifier, GraphComplexNode source, GraphComplexNode target) {
        return addEdgeBetween(new GraphComplexEdge(identifier), source, target);
    }

    @Override
    public int addEdgeBetween(GraphComplexNode source, GraphComplexNode target) {
        return addEdgeBetween(nextEdgeIdentifier(), source, target);
    }

    public void addBindingSite(ChemicalEntity first, ChemicalEntity second) {
        getNode(node -> node.getEntity().equals(first))
                .ifPresent(node -> node.addBindingSite(BindingSite.forPair(first, second)));
    }

    public void addBindingSite(ChemicalEntity first, BindingSite bindingSite) {
        getNode(node -> node.getEntity().equals(first))
                .ifPresent(node -> node.addBindingSite(bindingSite));
    }

    public boolean containsEntity(ChemicalEntity entity) {
        return getNodes().stream()
                .anyMatch(node -> node.getEntity().equals(entity));
    }

    @Override
    public Integer nextNodeIdentifier() {
        if (getNodes().isEmpty()) {
            return 0;
        }
        return getNodes().size();
    }

    public Optional<GraphComplex> bind(GraphComplex otherComplex, BindingSite bindingSite) {
        GraphComplex thisCopy = getCopy();
        Optional<GraphComplexNode> firstPartner = thisCopy.getNodeWithUnoccupiedBindingSite(bindingSite);
        Optional<GraphComplexNode> secondPartner = otherComplex.getNodeWithUnoccupiedBindingSite(bindingSite);
        if (firstPartner.isPresent() && secondPartner.isPresent()) {
            thisCopy.combine(firstPartner.get(), secondPartner.get(), otherComplex);
            return Optional.of(thisCopy);
        }
        return Optional.empty();
    }

    public Optional<GraphComplexNode> getNodeWithUnoccupiedBindingSite(BindingSite bindingSite) {
        for (GraphComplexNode node : getNodes()) {
            if (node.hasUnoccupiedBindingSite(bindingSite)) {
                return Optional.of(node);
            }
        }
        return Optional.empty();
    }

    public void combine(GraphComplexNode first, GraphComplexNode second, GraphComplex secondGraph) {
        // assuming that there are no isolated nodes
        int nodeOffset = getNodes().size();
        for (GraphComplexNode node : secondGraph.getNodes()) {
            GraphComplexNode copy = node.getCopy(nodeOffset);
            addNode(copy);
        }
        for (GraphComplexEdge edge : getEdges()) {
            GraphComplexEdge edgeCopy = edge.getCopy(getEdges().size());
            GraphComplexNode source = getNode(edge.getSource().getIdentifier() + nodeOffset);
            GraphComplexNode target = getNode(edge.getTarget().getIdentifier() + nodeOffset);
            addEdgeBetween(edgeCopy, source, target);
        }
        addEdgeBetween(first, getNode(second.getIdentifier() + nodeOffset));
    }

    public Optional<List<GraphComplex>> unbind(BindingSite bindingSite) {
        GraphComplex thisCopy = getCopy();
        Optional<Pair<GraphComplexNode>> bindingPairOptional = thisCopy.findBindingPairFor(bindingSite);
        if (!bindingPairOptional.isPresent()) {
            return Optional.empty();
        }
        Pair<GraphComplexNode> nodePair = bindingPairOptional.get();
        thisCopy.removeEdge(nodePair.getFirst(), nodePair.getSecond());
        List<GraphComplex> subgraphs = Graphs.findDisconnectedSubgraphs(thisCopy);
        if (subgraphs.size() != 2) {
            return Optional.empty();
        }
        return Optional.of(subgraphs);
    }

    public Optional<GraphComplex> remove(ChemicalEntity chemicalEntity, BindingSite bindingSite) {
        GraphComplex thisCopy = getCopy();
        Optional<Pair<GraphComplexNode>> nodePairOptional = thisCopy.findBindingPairFor(bindingSite);
        if (nodePairOptional.isPresent()) {
            Pair<GraphComplexNode> nodePair = nodePairOptional.get();
            thisCopy.removeEdge(nodePair.getFirst(), nodePair.getSecond());
            List<GraphComplex> subgraphs = Graphs.findDisconnectedSubgraphs(thisCopy);
            for (GraphComplex subgraph : subgraphs) {
                if (!subgraph.containsNode(node -> node.getEntity().equals(chemicalEntity))) {
                    return Optional.of(subgraph);
                }
            }
        }
        return Optional.empty();
    }

    public Optional<Pair<GraphComplexNode>> findBindingPairFor(BindingSite bindingSite) {
        for (GraphComplexNode node : getNodes()) {
            if (node.hasOccupiedBindingSite(bindingSite)) {
                for (GraphComplexNode potentialPartner : node.getNeighbours()) {
                    if (potentialPartner.hasBindingSite(bindingSite)) {
                        return Optional.of(new Pair<>(node, potentialPartner));
                    }
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public GraphComplex getCopy() {
        return new GraphComplex(this, 0, 0);
    }

    public GraphComplex getCopy(int nodeOffset, int edgeOffset) {
        return new GraphComplex(this, nodeOffset, edgeOffset);
    }

    @Override
    public String toString() {
        return getNodes().stream()
                .sorted(Comparator.comparing(Node::getIdentifier))
                .map(GraphComplexNode::getEntity)
                .map(ChemicalEntity::getIdentifier)
                .collect(Collectors.joining("-"));
    }
}
