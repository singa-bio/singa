package bio.singa.chemistry.entities.graphcomplex;

import bio.singa.chemistry.annotations.Annotation;
import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.chemistry.entities.SmallMolecule;
import bio.singa.chemistry.features.ChemistryFeatureContainer;
import bio.singa.features.identifiers.SimpleStringIdentifier;
import bio.singa.features.identifiers.model.Identifier;
import bio.singa.features.model.Feature;
import bio.singa.features.model.FeatureContainer;
import bio.singa.mathematics.graphs.model.AbstractMapGraph;
import bio.singa.mathematics.graphs.model.Graphs;
import bio.singa.mathematics.graphs.model.IdentifierSupplier;
import bio.singa.mathematics.vectors.Vector2D;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author cl
 */
public class GraphComplex extends AbstractMapGraph<GraphComplexNode, GraphComplexEdge, Vector2D, Integer> implements ChemicalEntity {

    public static final Set<Class<? extends Feature>> availableFeatures = new HashSet<>();

    public static GraphComplex from(ChemicalEntity chemicalEntity) {
        GraphComplex graph = new GraphComplex();
        GraphComplexNode node = new GraphComplexNode(graph.nextNodeIdentifier());
        node.setEntity(chemicalEntity);
        graph.addNode(node);
        graph.updateIdentifier();
        return graph;
    }

    public static GraphComplex from(ChemicalEntity chemicalEntity, BindingSite bindingSite) {
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
    private boolean membraneBound;
    private List<Annotation> annotations;
    private FeatureContainer features;

    private IdentifierSupplier nodeSupplier;
    private IdentifierSupplier edgeSupplier;

    public GraphComplex() {
        annotations = new ArrayList<>();
        features = new ChemistryFeatureContainer();
        nodeSupplier = new IdentifierSupplier();
        edgeSupplier = new IdentifierSupplier();
    }

    private GraphComplex(GraphComplex graphComplex) {
        this();
        Map<Integer, Integer> identifierMapping = new HashMap<>();
        for (GraphComplexNode node : graphComplex.getNodes()) {
            // conserve identifier
            int newIdentifier = nextNodeIdentifier();
            Integer oldIdentifier = node.getIdentifier();
            identifierMapping.put(oldIdentifier, newIdentifier);
            // create copy
            GraphComplexNode copy = node.getCopy();
            copy.setIdentifier(newIdentifier);
            addNode(copy);
        }
        for (GraphComplexEdge edge : graphComplex.getEdges()) {
            GraphComplexEdge edgeCopy = edge.getCopy();
            edgeCopy.setIdentifier(nextEdgeIdentifier());
            GraphComplexNode source = getNode(identifierMapping.get(edge.getSource().getIdentifier()));
            GraphComplexNode target = getNode(identifierMapping.get(edge.getTarget().getIdentifier()));
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

    public int addEdgeBetween(GraphComplexNode source, GraphComplexNode target, BindingSite bindingSite) {
        GraphComplexEdge graphComplexEdge = new GraphComplexEdge(nextEdgeIdentifier());
        graphComplexEdge.setConnectedSite(bindingSite);
        return addEdgeBetween(graphComplexEdge, source, target);
    }

    public void addBindingSite(ChemicalEntity first, BindingSite bindingSite) {
        getNode(node -> node.getEntity().equals(first))
                .ifPresent(node -> node.addBindingSite(bindingSite));
    }

    public boolean containsEntity(ChemicalEntity entity) {
        return getNodes().stream()
                .anyMatch(node -> node.isEntity(entity));
    }

    public long countParts(ChemicalEntity entity) {
        return getNodes().stream()
                .filter(node -> node.isEntity(entity))
                .count();
    }

    @Override
    public Integer nextNodeIdentifier() {
        return nodeSupplier.getAndIncrement();
    }

    @Override
    public int nextEdgeIdentifier() {
        return edgeSupplier.getAndIncrement();
    }

    @Override
    public boolean isMembraneBound() {
        return membraneBound;
    }

    @Override
    public void setMembraneBound(boolean membraneBound) {
        this.membraneBound = membraneBound;
    }

    @Override
    public List<Annotation> getAnnotations() {
        return annotations;
    }

    @Override
    public List<Identifier> getAllIdentifiers() {
        List<Identifier> identifiers = features.getAdditionalIdentifiers();
        identifiers.add(new SimpleStringIdentifier(identifier));
        return identifiers;
    }

    @Override
    public Collection<Feature<?>> getFeatures() {
        return features.getAllFeatures();
    }

    @Override
    public <FeatureType extends Feature> FeatureType getFeature(Class<FeatureType> featureTypeClass) {
        if (!features.hasFeature(featureTypeClass)) {
            setFeature(featureTypeClass);
        }
        return features.getFeature(featureTypeClass);
    }

    @Override
    public <FeatureType extends Feature> void setFeature(Class<FeatureType> featureTypeClass) {
        features.setFeature(featureTypeClass, this);
    }

    @Override
    public <FeatureType extends Feature> void setFeature(FeatureType feature) {
        features.setFeature(feature);
    }

    @Override
    public <FeatureType extends Feature> boolean hasFeature(Class<FeatureType> featureTypeClass) {
        return features.hasFeature(featureTypeClass);
    }

    @Override
    public Set<Class<? extends Feature>> getAvailableFeatures() {
        return availableFeatures;
    }

    @Override
    public GraphComplex getCopy() {
        return new GraphComplex(this);
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
        Map<Integer, Integer> identifierMapping = new HashMap<>();
        for (GraphComplexNode node : secondGraph.getNodes()) {
            // conserve identifier
            int newIdentifier = nextNodeIdentifier();
            Integer oldIdentifier = node.getIdentifier();
            identifierMapping.put(oldIdentifier, newIdentifier);
            // create copy
            GraphComplexNode copy = node.getCopy();
            copy.setIdentifier(newIdentifier);
            addNode(copy);
        }
        for (GraphComplexEdge edge : secondGraph.getEdges()) {
            GraphComplexEdge edgeCopy = edge.getCopy();
            edgeCopy.setIdentifier(nextEdgeIdentifier());
            GraphComplexNode source = getNode(identifierMapping.get(edge.getSource().getIdentifier()));
            GraphComplexNode target = getNode(identifierMapping.get(edge.getTarget().getIdentifier()));
            addEdgeBetween(edgeCopy, source, target);
        }
        addEdgeBetween(first, getNode(identifierMapping.get(second.getIdentifier())), bindingSite);
        updateIdentifier();
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

    private String generateIdentifier() {
        return getNodes().stream()
                .map(GraphComplexNode::getEntity)
                .map(ChemicalEntity::getIdentifier)
                .sorted()
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GraphComplex that = (GraphComplex) o;
        return Objects.equals(identifier, that.identifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier);
    }

}
