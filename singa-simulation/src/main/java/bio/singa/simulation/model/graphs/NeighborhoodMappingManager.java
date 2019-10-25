package bio.singa.simulation.model.graphs;

import bio.singa.core.utility.Pair;
import bio.singa.features.parameters.Environment;
import bio.singa.features.units.UnitRegistry;
import bio.singa.mathematics.algorithms.graphs.ShortestPathFinder;
import bio.singa.mathematics.geometry.edges.LineSegment;
import bio.singa.mathematics.geometry.edges.SimpleLineSegment;
import bio.singa.mathematics.geometry.faces.Polygons;
import bio.singa.mathematics.geometry.model.Polygon;
import bio.singa.mathematics.graphs.model.*;
import bio.singa.mathematics.vectors.Vector2D;
import bio.singa.simulation.features.Ratio;
import bio.singa.simulation.model.agents.surfacelike.MembraneSegment;
import bio.singa.simulation.model.sections.CellSubsection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author cl
 */
public class NeighborhoodMappingManager {

    private static final Logger logger = LoggerFactory.getLogger(NeighborhoodMappingManager.class);

    private AutomatonNode currentNode;
    private double unitSystemLength;

    public NeighborhoodMappingManager() {
        unitSystemLength = Environment.convertSystemToSimulationScale(UnitRegistry.getSpace());
    }

    public static void initializeNeighborhoodForGraph(AutomatonGraph graph) {
        NeighborhoodMappingManager manager = new NeighborhoodMappingManager();
        for (AutomatonNode node : graph.getNodes()) {
            manager.currentNode = node;
            manager.initializeNeighborhoodForNode();
            manager.initializeConnectedMembrane();
        }
    }

    private void initializeNeighborhoodForNode() {
        for (Map.Entry<CellSubsection, Polygon> currentSubsectionEntry : currentNode.getSubsectionRepresentations().entrySet()) {
            CellSubsection currentSubsection = currentSubsectionEntry.getKey();
            Polygon currentPolygon = currentSubsectionEntry.getValue();
            for (AutomatonNode neighbour : currentNode.getNeighbours()) {
                Map<CellSubsection, Polygon> neighborSubsections = neighbour.getSubsectionRepresentations();
                for (Map.Entry<CellSubsection, Polygon> neighborSubsectionEntry : neighborSubsections.entrySet()) {
                    CellSubsection neighborSubsection = neighborSubsectionEntry.getKey();
                    // skip already initialized sections
                    if (containsMapping(currentSubsection, neighbour)) {
                        continue;
                    }
                    Polygon neighborPolygon = neighborSubsectionEntry.getValue();
                    // the first element of the pair is the frist argument entering the getTouchingLineSegments method
                    Map<Pair<LineSegment>, LineSegment> touchingLineSegments = Polygons.getTouchingLineSegments(currentPolygon, neighborPolygon);
                    // skip subsection that dont overlap
                    if (touchingLineSegments.isEmpty()) {
                        continue;
                    }
                    if (touchingLineSegments.size() > 1) {
                        logger.warn("More than one line segment touch between node {} and {}. By contract neighbouring " +
                                "nodes should only touch once.", currentNode.getStringIdentifier(), neighbour.getStringIdentifier());
                    }
                    Map.Entry<Pair<LineSegment>, LineSegment> entry = touchingLineSegments.entrySet().iterator().next();
                    // skip point like segments
                    if (entry.getValue().getLength() < 1e-8) {
                        continue;
                    }
                    double relativeAdjacentArea = entry.getValue().getLength() / unitSystemLength;
                    double relativeCentroidDistance = currentPolygon.getCentroid().distanceTo(neighborPolygon.getCentroid()) / unitSystemLength;
                    double relativeEffectiveArea = relativeAdjacentArea / (relativeCentroidDistance * relativeCentroidDistance);

                    if (relativeEffectiveArea > 0) {
                        AutomatonNode.AreaMapping mapping = new AutomatonNode.AreaMapping(currentNode, neighbour, neighborSubsection, relativeEffectiveArea);
                        addAreaMapping(currentSubsection, mapping);
                        neighbour.addAreaMapping(currentSubsection, mapping);
                    }

                }
            }
        }
    }

    private void addAreaMapping(CellSubsection subsection, AutomatonNode.AreaMapping mapping) {
        Map<CellSubsection, List<AutomatonNode.AreaMapping>> subsectionAdjacency = currentNode.getSubsectionAdjacency();
        if (!subsectionAdjacency.containsKey(subsection)) {
            subsectionAdjacency.put(subsection, new ArrayList<>());
        }
        subsectionAdjacency.get(subsection).add(mapping);
    }

    private boolean containsMapping(CellSubsection subsection, AutomatonNode neighbour) {
        Map<CellSubsection, List<AutomatonNode.AreaMapping>> subsectionAdjacency = currentNode.getSubsectionAdjacency();
        if (!subsectionAdjacency.containsKey(subsection)) {
            return false;
        }
        return subsectionAdjacency.get(subsection).stream()
                .anyMatch(entry -> entry.getTarget().equals(currentNode) && entry.getSource().equals(neighbour));
    }

    private void initializeConnectedMembrane() {
        UndirectedGraph graph = new UndirectedGraph();
        for (MembraneSegment membraneSegment : currentNode.getMembraneSegments()) {
            RegularNode start = graph.snapNode(membraneSegment.getStartingPoint());
            RegularNode end = graph.snapNode(membraneSegment.getEndingPoint());
            graph.addEdgeBetween(start, end);
        }

        Optional<RegularNode> pathStartOptional = graph.getNode(GraphPredicates::isLeafNode);
        if (!pathStartOptional.isPresent()) {
            return;
        }
        RegularNode pathStart = pathStartOptional.get();

        Optional<RegularNode> pathEndOptional = graph.getNode(node -> GraphPredicates.isLeafNode(node) && !GraphPredicates.haveSameIdentifiers(node, pathStart));
        if (!pathEndOptional.isPresent()) {
            return;
        }
        RegularNode pathEnd = pathEndOptional.get();

        GraphPath<RegularNode, UndirectedEdge> path = ShortestPathFinder.findBasedOnPredicate(graph, pathStart, node -> GraphPredicates.haveSameIdentifiers(node, pathEnd));
        for (RegularNode node : path.getNodes()) {
            currentNode.getMembraneVectors().add(node.getPosition());
        }

    }

    public static void initializeDiffusiveReduction(AutomatonNode node, Polygon area, Ratio reductionRatio) {
        double cortexRatio = reductionRatio.getContent().getValue().doubleValue();
        for (Map.Entry<CellSubsection, Polygon> currentSubsectionEntry : node.getSubsectionRepresentations().entrySet()) {
            CellSubsection currentSubsection = currentSubsectionEntry.getKey();
            Polygon currentPolygon = currentSubsectionEntry.getValue();
            Vector2D currentCentroid = currentPolygon.getCentroid();
            boolean currentIsInArea = currentCentroid.isInside(area);
            for (AutomatonNode neighbour : node.getNeighbours()) {
                Map<CellSubsection, Polygon> neighborSubsections = neighbour.getSubsectionRepresentations();
                for (Map.Entry<CellSubsection, Polygon> neighborSubsectionEntry : neighborSubsections.entrySet()) {
                    CellSubsection neighborSubsection = neighborSubsectionEntry.getKey();
                    Polygon neighborPolygon = neighborSubsectionEntry.getValue();
                    Vector2D neighborCentroid = neighborPolygon.getCentroid();
                    boolean neighborIsInArea = neighborCentroid.isInside(area);
                    AutomatonNode.AreaMapping mapping = getCorrectMapping(neighbour, node.getSubsectionAdjacency().get(currentSubsection), neighborSubsection);
                    // skip non adjacent subsections
                    if (mapping == null) {
                        continue;
                    }
                    if (currentIsInArea && neighborIsInArea) {
                        mapping.setDiffusiveRatio(cortexRatio);
                    } else if (currentIsInArea || neighborIsInArea) {
                        // determine area that is affected
                        Set<Vector2D> intersections = area.getIntersections(new SimpleLineSegment(currentCentroid, neighborCentroid));
                        if (intersections.size() == 1) {
                            Vector2D intersection = intersections.iterator().next();
                            double totalDistance = currentCentroid.distanceTo(neighborCentroid);
                            double distanceToCurrent = intersection.distanceTo(currentCentroid) / totalDistance;
                            double distanceToNeighbor = intersection.distanceTo(neighborCentroid) / totalDistance;
                            double diffusiveRatio;
                            if (currentIsInArea) {
                                diffusiveRatio = distanceToCurrent * cortexRatio + distanceToNeighbor;
                            } else {
                                diffusiveRatio = distanceToNeighbor * cortexRatio + distanceToCurrent;
                            }
                            mapping.setDiffusiveRatio(diffusiveRatio);
                        }
                    }
                }
            }
        }
    }

    private static AutomatonNode.AreaMapping getCorrectMapping(AutomatonNode node, List<AutomatonNode.AreaMapping> mappings, CellSubsection subsection) {
        for (AutomatonNode.AreaMapping mapping : mappings) {
            if (mapping.getSource().equals(node) && mapping.getSubsection().equals(subsection)) {
                return mapping;
            }
        }
        return null;
    }

}
