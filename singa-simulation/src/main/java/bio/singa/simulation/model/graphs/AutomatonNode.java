package bio.singa.simulation.model.graphs;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.core.utility.Pair;
import bio.singa.features.parameters.Environment;
import bio.singa.features.units.UnitRegistry;
import bio.singa.mathematics.algorithms.graphs.ShortestPathFinder;
import bio.singa.mathematics.geometry.edges.LineSegment;
import bio.singa.mathematics.geometry.faces.Polygons;
import bio.singa.mathematics.geometry.model.Polygon;
import bio.singa.mathematics.graphs.model.*;
import bio.singa.mathematics.topology.grids.rectangular.RectangularCoordinate;
import bio.singa.mathematics.vectors.Vector2D;
import bio.singa.simulation.model.agents.linelike.LineLikeAgent;
import bio.singa.simulation.model.agents.surfacelike.MembraneSegment;
import bio.singa.simulation.model.modules.concentration.ConcentrationDelta;
import bio.singa.simulation.model.modules.concentration.ConcentrationDeltaManager;
import bio.singa.simulation.model.sections.CellRegion;
import bio.singa.simulation.model.sections.CellRegions;
import bio.singa.simulation.model.sections.CellSubsection;
import bio.singa.simulation.model.sections.ConcentrationContainer;
import bio.singa.simulation.model.simulation.Updatable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.units.indriya.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.quantity.Area;
import java.util.*;

/**
 * A node of the {@link AutomatonGraph}. Contains the concentrations of {@link ChemicalEntity}s
 * in a {@link ConcentrationContainer} and other attributes. Each node holds references to its neighbour nodes.
 *
 * @author cl
 */
public class AutomatonNode extends AbstractNode<AutomatonNode, Vector2D, RectangularCoordinate> implements Updatable {

    private static final Logger logger = LoggerFactory.getLogger(AutomatonNode.class);

    private CellRegion cellRegion;
    private ConcentrationDeltaManager concentrationManager;
    private Polygon spatialRepresentation;
    private Map<LineLikeAgent, Set<Vector2D>> microtubuleSegments;
    private List<MembraneSegment> membraneSegments;
    private List<Vector2D> membraneVectors;

    private Map<CellSubsection, Polygon> subsectionRepresentations;
    private Map<CellSubsection, List<AreaMapping>> subsectionAdjacency;

    private Quantity<Area> membraneArea;

    public AutomatonNode(RectangularCoordinate identifier) {
        super(identifier);
        setPosition(new Vector2D());
        microtubuleSegments = new HashMap<>();
        subsectionRepresentations = new HashMap<>();
        subsectionAdjacency = new HashMap<>();
        membraneSegments = new ArrayList<>();
        membraneVectors = new ArrayList<>();
        cellRegion = CellRegions.EXTRACELLULAR_REGION;
        concentrationManager = new ConcentrationDeltaManager(cellRegion.setUpConcentrationContainer());
    }

    public AutomatonNode(int column, int row) {
        this(new RectangularCoordinate(column, row));
    }

    public void initializeAdjacency() {
        double defaultLength = Environment.convertSystemToSimulationScale(UnitRegistry.getSpace());
        for (Map.Entry<CellSubsection, Polygon> currentSubsectionEntry : subsectionRepresentations.entrySet()) {
            CellSubsection currentSubsection = currentSubsectionEntry.getKey();
            Polygon currentPolygon = currentSubsectionEntry.getValue();
            for (AutomatonNode neighbour : getNeighbours()) {
                Map<CellSubsection, Polygon> neighborSubsections = neighbour.getSubsectionRepresentations();
                for (Map.Entry<CellSubsection, Polygon> neighborSubsectionEntry : neighborSubsections.entrySet()) {
                    CellSubsection neighborSubsection = neighborSubsectionEntry.getKey();
                    Polygon neighborPolygon = neighborSubsectionEntry.getValue();
                    // the first element of the pair is the frist argument entering the getTouchingLineSegments method
                    Map<Pair<LineSegment>, LineSegment> touchingLineSegments = Polygons.getTouchingLineSegments(currentPolygon, neighborPolygon);
                    // skip subsection that dont overlap
                    if (touchingLineSegments.isEmpty()) {
                        continue;
                    }
                    if (touchingLineSegments.size() > 1) {
                        logger.warn("More than one line segment touch between node {} and {}. By contract neighbouring nodes should only touch once.", getStringIdentifier(), neighbour.getStringIdentifier());
                    }
                    Map.Entry<Pair<LineSegment>, LineSegment> entry = touchingLineSegments.entrySet().iterator().next();
                    // skip point like segments
                    if (entry.getValue().getLength() < 1e-8) {
                        continue;
                    }
                    double relativeAdjacentArea = entry.getValue().getLength() / defaultLength;
                    double relativeCentroidDistance = currentPolygon.getCentroid().distanceTo(neighborPolygon.getCentroid()) / defaultLength;
                    double relativeEffectiveArea = relativeAdjacentArea / Math.sqrt(relativeCentroidDistance);
                    // TODO maybe add to neighbor map as well
                    if (relativeEffectiveArea > 0) {
                        if (!subsectionAdjacency.containsKey(currentSubsection)) {
                            subsectionAdjacency.put(currentSubsection, new ArrayList<>());
                        }
                        subsectionAdjacency.get(currentSubsection).add(new AreaMapping(neighbour, neighborSubsection, relativeEffectiveArea));
                    }
                }
            }
        }
        initializeConnectedMembrane();
    }

    private void initializeConnectedMembrane() {

        UndirectedGraph nodeGraph = new UndirectedGraph();
        for (MembraneSegment membraneSegment : membraneSegments) {
            Vector2D startingPoint = membraneSegment.getStartingPoint();
            RegularNode start = nodeGraph.addNodeIf(node -> node.getPosition().equals(startingPoint), new RegularNode(nodeGraph.nextNodeIdentifier(), startingPoint));
            Vector2D endingPoint = membraneSegment.getEndingPoint();
            RegularNode end = nodeGraph.addNodeIf(node -> node.getPosition().equals(endingPoint), new RegularNode(nodeGraph.nextNodeIdentifier(), endingPoint));
            nodeGraph.addEdgeBetween(start, end);
        }

        Optional<RegularNode> pathStartOptional = nodeGraph.getNode(node -> node.getDegree() == 1);
        if (pathStartOptional.isPresent()) {
            RegularNode pathStart = pathStartOptional.get();
            Optional<RegularNode> pathEndOptional = nodeGraph.getNode(node -> node.getDegree() == 1 && !node.getIdentifier().equals(pathStart.getIdentifier()));
            if (pathEndOptional.isPresent()) {
                RegularNode pathEnd = pathEndOptional.get();
                GraphPath<RegularNode, UndirectedEdge> path = ShortestPathFinder.findBasedOnPredicate(nodeGraph, pathStart, node -> node.getIdentifier().equals(pathEnd.getIdentifier()));
                for (RegularNode node : path.getNodes()) {
                    membraneVectors.add(node.getPosition());
                }
            }
        }
    }

    public Map<CellSubsection, List<AreaMapping>> getSubsectionAdjacency() {
        return subsectionAdjacency;
    }

    public List<Vector2D> getMembraneVectors() {
        return membraneVectors;
    }

    public Map<CellSubsection, Polygon> getSubsectionRepresentations() {
        return subsectionRepresentations;
    }

    /**
     * Adds a potential delta to this node.
     *
     * @param potentialDelta The potential delta.
     */
    public void addPotentialDelta(ConcentrationDelta potentialDelta) {
        concentrationManager.addPotentialDelta(potentialDelta);
    }

    @Override
    public ConcentrationDeltaManager getConcentrationManager() {
        return concentrationManager;
    }

    /**
     * Returns all referenced sections in this node.
     *
     * @return all referenced sections in this node.
     */
    public Set<CellSubsection> getAllReferencedSections() {
        return concentrationManager.getConcentrationContainer().getReferencedSubsections();
    }

    /**
     * Returns {@code true} if this node is observed.
     *
     * @return {@code true} if this node is observed.
     */
    public boolean isObserved() {
        return concentrationManager.isObserved();
    }

    /**
     * Sets the observed state of this node.
     *
     * @param isObserved {@code true} if this node is observed.
     */
    public void setObserved(boolean isObserved) {
        concentrationManager.setObserved(isObserved);
    }

    @Override
    public CellRegion getCellRegion() {
        return cellRegion;
    }

    public void setCellRegion(CellRegion cellRegion) {
        this.cellRegion = cellRegion;
        concentrationManager.setConcentrationContainer(cellRegion.setUpConcentrationContainer());
    }

    @Override
    public String getStringIdentifier() {
        return "n" + getIdentifier().toString();
    }

    /**
     * Returns the {@link ConcentrationContainer} used by this node.
     *
     * @return The {@link ConcentrationContainer} used by this node.
     */
    public ConcentrationContainer getConcentrationContainer() {
        return concentrationManager.getConcentrationContainer();
    }

    /**
     * Sets the {@link ConcentrationContainer} for this node.
     *
     * @param concentrationContainer The {@link ConcentrationContainer} for this node.
     */
    public void setConcentrationContainer(ConcentrationContainer concentrationContainer) {
        concentrationManager.setConcentrationContainer(concentrationContainer);
    }

    public Polygon getSpatialRepresentation() {
        return spatialRepresentation;
    }

    public void setSpatialRepresentation(Polygon spatialRepresentation) {
        this.spatialRepresentation = spatialRepresentation;
        subsectionRepresentations.put(getCellRegion().getInnerSubsection(), spatialRepresentation);
    }

    public void setConcentrationManager(ConcentrationDeltaManager concentrationManager) {
        this.concentrationManager = concentrationManager;
    }

    public Map<LineLikeAgent, Set<Vector2D>> getAssociatedLineLikeAgents() {
        return microtubuleSegments;
    }

    public void addLineLikeAgentSegment(LineLikeAgent lineLikeAgent, Vector2D segment) {
        if (!microtubuleSegments.containsKey(lineLikeAgent)) {
            microtubuleSegments.put(lineLikeAgent, new HashSet<>());
        }
        microtubuleSegments.get(lineLikeAgent).add(segment);
    }

    public List<MembraneSegment> getMembraneSegments() {
        return membraneSegments;
    }

    public void addMembraneSegment(MembraneSegment segment) {
        membraneSegments.add(segment);
    }

    public Quantity<Area> getMembraneArea() {
        if (membraneArea == null) {
            membraneArea = Quantities.getQuantity(0.0, UnitRegistry.getAreaUnit());
            for (MembraneSegment membraneSegment : membraneSegments) {
                membraneArea = membraneArea.add(membraneSegment.getArea());
            }
        }
        return membraneArea;
    }

    public void addSubsectionRepresentation(CellSubsection subsection, Polygon representation) {
        subsectionRepresentations.put(subsection, representation);
    }

    @Override
    public String toString() {
        return "Node " + getIdentifier() + " (" + cellRegion + ")";
    }

    @Override
    public AutomatonNode getCopy() {
        throw new UnsupportedOperationException("not implemented");
    }


    public static class AreaMapping {

        private final AutomatonNode node;
        private final CellSubsection subsection;
        private final double relativeArea;

        public AreaMapping(AutomatonNode node, CellSubsection subsection, double relativeArea) {
            this.node = node;
            this.subsection = subsection;
            this.relativeArea = relativeArea;
        }

        public AutomatonNode getNode() {
            return node;
        }

        public CellSubsection getSubsection() {
            return subsection;
        }

        public double getRelativeArea() {
            return relativeArea;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            AreaMapping that = (AreaMapping) o;
            return Objects.equals(node, that.node) &&
                    Objects.equals(subsection, that.subsection);
        }

        @Override
        public int hashCode() {
            return Objects.hash(node, subsection);
        }
    }


}
