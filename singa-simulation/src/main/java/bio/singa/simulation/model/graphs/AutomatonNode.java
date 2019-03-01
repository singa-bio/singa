package bio.singa.simulation.model.graphs;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.features.units.UnitRegistry;
import bio.singa.mathematics.geometry.model.Polygon;
import bio.singa.mathematics.graphs.model.AbstractNode;
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
import tec.uom.se.quantity.Quantities;

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

    private CellRegion cellRegion;
    private ConcentrationDeltaManager concentrationManager;
    private Polygon spatialRepresentation;
    private Map<LineLikeAgent, Set<Vector2D>> microtubuleSegments;
    private List<MembraneSegment> membraneSegments;
    private Map<CellSubsection, Polygon> subsectionRepresentations;

    private Quantity<Area> membraneArea;

    public AutomatonNode(RectangularCoordinate identifier) {
        super(identifier);
        setPosition(new Vector2D());
        microtubuleSegments = new HashMap<>();
        subsectionRepresentations = new HashMap<>();
        membraneSegments = new ArrayList<>();
        cellRegion = CellRegions.EXTRACELLULAR_REGION;
        concentrationManager = new ConcentrationDeltaManager(cellRegion.setUpConcentrationContainer());
    }

    public AutomatonNode(int column, int row) {
        this(new RectangularCoordinate(column, row));
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
        return "Node " + getIdentifier().toString();
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

}
