package bio.singa.chemistry.entities.graphcomplex;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.chemistry.entities.SmallMolecule;
import bio.singa.mathematics.graphs.model.AbstractMapGraph;
import bio.singa.mathematics.graphs.model.Graphs;
import bio.singa.mathematics.graphs.model.Node;
import bio.singa.mathematics.vectors.Vector2D;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author cl
 */
public class GraphComplex extends AbstractMapGraph<GraphComplexNode, GraphComplexEdge, Vector2D, Integer> {

    public static GraphComplex from(ChemicalEntity chemicalEntity) {
        GraphComplex graph = new GraphComplex();
        GraphComplexNode node = new GraphComplexNode(graph.nextNodeIdentifier());
        node.setEntity(chemicalEntity);
        graph.addNode(node);
        graph.updateIdentifier();
        return graph;
    }

    static GraphComplex from(ChemicalEntity chemicalEntity, BindingSite bindingSite) {
        GraphComplex graph = from(chemicalEntity);
        graph.addBindingSite(chemicalEntity, bindingSite);
        graph.updateIdentifier();
        return graph;
    }

    public static GraphComplex from(ChemicalEntity chemicalEntity, Collection<BindingSite> bindingSites) {
        GraphComplex graph = from(chemicalEntity);
        for (BindingSite bindingSite : bindingSites) {
            graph.addBindingSite(chemicalEntity, bindingSite);
        }
        graph.updateIdentifier();
        return graph;
    }

    private String identifier;

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
            nextEdgeIdentifier();
        }
        updateIdentifier();
    }

    @Override
    public int addEdgeBetween(int identifier, GraphComplexNode source, GraphComplexNode target) {
        return addEdgeBetween(new GraphComplexEdge(identifier), source, target);
    }

    @Override
    public int addEdgeBetween(GraphComplexNode source, GraphComplexNode target) {
        return addEdgeBetween(nextEdgeIdentifier(), source, target);
    }

    public int addEdgeBetween(GraphComplexNode source, GraphComplexNode target, BindingSite bindingSite) {
        GraphComplexEdge graphComplexEdge = new GraphComplexEdge(nextEdgeIdentifier());
        graphComplexEdge.setConnectedSite(bindingSite);
        return addEdgeBetween(graphComplexEdge, source, target);
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
            thisCopy.combine(firstPartner.get(), secondPartner.get(), otherComplex, bindingSite);
            return Optional.of(thisCopy);
        }
        return Optional.empty();
    }

    public Optional<GraphComplex> add(ChemicalEntity otherEntity, BindingSite bindingSite) {
        return bind(GraphComplex.from(otherEntity, bindingSite), bindingSite);
    }

    public void combine(GraphComplexNode first, GraphComplexNode second, GraphComplex secondGraph, BindingSite bindingSite) {
        // assuming that there are no isolated nodes
        int nodeOffset = getNodes().size();
        for (GraphComplexNode node : secondGraph.getNodes()) {
            GraphComplexNode copy = node.getCopy(nodeOffset);
            addNode(copy);
        }
        int edgeOffset = getEdges().size();
        for (GraphComplexEdge edge : secondGraph.getEdges()) {
            GraphComplexEdge edgeCopy = edge.getCopy(edgeOffset);
            GraphComplexNode source = getNode(edge.getSource().getIdentifier() + nodeOffset);
            GraphComplexNode target = getNode(edge.getTarget().getIdentifier() + nodeOffset);
            addEdgeBetween(edgeCopy, source, target);
        }
        addEdgeBetween(first, getNode(second.getIdentifier() + nodeOffset), bindingSite);
        updateIdentifier();
    }

    @Override
    public int nextEdgeIdentifier() {
        if (getEdges().isEmpty()) {
            return 0;
        }
        return getEdges().size();
    }

    public Optional<List<GraphComplex>> unbind(BindingSite bindingSite) {
        GraphComplex thisCopy = getCopy();
        Optional<GraphComplexEdge> edgeOptional = thisCopy.getEdge(edge -> edge.getConnectedSite().equals(bindingSite));
        if (!edgeOptional.isPresent()) {
            return Optional.empty();
        }
        thisCopy.removeEdge(edgeOptional.get());
        List<GraphComplex> subgraphs = Graphs.findDisconnectedSubgraphs(thisCopy);
        subgraphs.forEach(GraphComplex::updateIdentifier);
        if (subgraphs.size() != 2) {
            return Optional.empty();
        }
        return Optional.of(subgraphs);
    }

    public Optional<GraphComplex> remove(ChemicalEntity chemicalEntity, BindingSite bindingSite) {
        GraphComplex thisCopy = getCopy();
        Optional<GraphComplexEdge> edgeOptional = thisCopy.getEdge(edge -> edge.getConnectedSite().equals(bindingSite));
        if (edgeOptional.isPresent()) {
            thisCopy.removeEdge(edgeOptional.get());
            List<GraphComplex> subgraphs = Graphs.findDisconnectedSubgraphs(thisCopy);
            for (GraphComplex subgraph : subgraphs) {
                if (!subgraph.containsNode(node -> node.getEntity().equals(chemicalEntity))) {
                    subgraph.updateIdentifier();
                    return Optional.of(subgraph);
                }
            }
        }
        return Optional.empty();
    }

    public Optional<GraphComplexNode> getNodeWithUnoccupiedBindingSite(BindingSite bindingSite) {
        List<GraphComplexNode> occupiedNodes = getEdges().stream()
                .filter(edge -> edge.getConnectedSite().equals(bindingSite))
                .flatMap(edge -> Stream.of(edge.getSource(), edge.getTarget()))
                .collect(Collectors.toList());

        return getNodes().stream()
                .filter(node -> !occupiedNodes.contains(node))
                .filter(node -> node.getBindingSites().contains(bindingSite))
                .filter(node -> {
                    if (node.getEntity() instanceof SmallMolecule) {
                        return node.getNeighbours().size() < 1;
                    }
                    return true;
                })
                .findAny();
    }

    @Override
    public GraphComplex getCopy() {
        return new GraphComplex(this, 0, 0);
    }

    public GraphComplex getCopy(int nodeOffset, int edgeOffset) {
        return new GraphComplex(this, nodeOffset, edgeOffset);
    }

    private String generateIdentifier() {
        return getNodes().stream()
                .sorted(Comparator.comparing(Node::getIdentifier))
                .map(GraphComplexNode::getEntity)
                .map(ChemicalEntity::getIdentifier)
                .collect(Collectors.joining("-"));
    }

    public void updateIdentifier() {
        identifier = generateIdentifier();
    }

    public String getIdentifier() {
        return identifier;
    }

    @Override
    public String toString() {
        return identifier;
    }
}
