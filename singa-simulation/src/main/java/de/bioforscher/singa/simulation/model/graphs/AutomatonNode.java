package de.bioforscher.singa.simulation.model.graphs;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.features.parameters.Environment;
import de.bioforscher.singa.features.quantities.MolarConcentration;
import de.bioforscher.singa.mathematics.geometry.model.Polygon;
import de.bioforscher.singa.mathematics.graphs.model.AbstractNode;
import de.bioforscher.singa.mathematics.topology.grids.rectangular.RectangularCoordinate;
import de.bioforscher.singa.mathematics.vectors.Vector2D;
import de.bioforscher.singa.simulation.model.compartments.CellSection;
import de.bioforscher.singa.simulation.model.compartments.CellSectionState;
import de.bioforscher.singa.simulation.model.compartments.EnclosedCompartment;
import de.bioforscher.singa.simulation.model.compartments.Membrane;
import de.bioforscher.singa.simulation.model.concentrations.ConcentrationContainer;
import de.bioforscher.singa.simulation.model.concentrations.SimpleConcentrationContainer;
import de.bioforscher.singa.simulation.model.layer.Vesicle;
import de.bioforscher.singa.simulation.modules.model.Delta;
import de.bioforscher.singa.simulation.modules.model.SimpleUpdateManager;
import de.bioforscher.singa.simulation.modules.model.Updatable;
import tec.uom.se.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.quantity.Area;
import java.util.*;

import static de.bioforscher.singa.features.units.UnitProvider.MOLE_PER_LITRE;
import static de.bioforscher.singa.simulation.model.compartments.CellSectionState.AQUEOUS;
import static de.bioforscher.singa.simulation.model.compartments.CellSectionState.MEMBRANE;

/**
 * A node of the {@link AutomatonGraph}. Contains the {@link CellSectionState}, the concentrations of {@link ChemicalEntity}s
 * in a {@link ConcentrationContainer}, the {@link CellSection}, and amongst other attributes. Each node holds
 * references to its neighbour nodes.
 *
 * @author cl
 */
public class AutomatonNode extends AbstractNode<AutomatonNode, Vector2D, RectangularCoordinate> implements Updatable {

    /**
     * The state.
     */
    private CellSectionState state;

    /**
     * A reference to the containing cell section.
     */
    private CellSection cellSection;

    /**
     * The contained chemical entities with their concentrations.
     */
    private ConcentrationContainer concentrationContainer;

    private SimpleUpdateManager updateManager;

    private Polygon spatialRepresentation;

    private Map<Vesicle, Quantity<Area>> associatedVesicles;

    /**
     * Creates a new plain automaton node. Initialized as {@link CellSectionState#AQUEOUS} in a "default" compartment with a
     * {@link SimpleConcentrationContainer}.
     *
     * @param identifier The identifier of the node.
     */
    public AutomatonNode(RectangularCoordinate identifier) {
        super(identifier);
        state = AQUEOUS;
        cellSection = new EnclosedCompartment("default", "Default Compartment");
        concentrationContainer = new SimpleConcentrationContainer(cellSection);
        updateManager = new SimpleUpdateManager(concentrationContainer);
        associatedVesicles = new HashMap<>();
    }

    public AutomatonNode(int column, int row) {
        this(new RectangularCoordinate(column, row));
    }

    /**
     * Sets the concentration of the given chemical entities.
     *
     * @param concentration The concentration in mol/l
     * @param entities The chemical entities.
     */
    public void setConcentrations(double concentration, ChemicalEntity... entities) {
        for (ChemicalEntity entity : entities) {
            setConcentration(entity, concentration);
        }
    }

    /**
     * Sets the concentration of the entity in mol/l.
     *
     * @param entity The entity.
     * @param concentration The concentration in mol/l.
     */
    public void setConcentration(ChemicalEntity entity, double concentration) {
        setConcentration(entity, Quantities.getQuantity(concentration, MOLE_PER_LITRE).to(Environment.getTransformedMolarConcentration()));
    }

    /**
     * Sets the concentration of the entity.
     *
     * @param entity The chemical entity.
     * @param quantity The quantity.
     */
    public void setConcentration(ChemicalEntity entity, Quantity<MolarConcentration> quantity) {
        concentrationContainer.setConcentration(entity, quantity);
    }

    /**
     * Returns all concentrations of all chemical entities.
     *
     * @return All concentrations of all chemical entities.
     */
    public Map<ChemicalEntity, Quantity<MolarConcentration>> getAllConcentrations() {
        return concentrationContainer.getAllConcentrations();
    }

    /**
     * Returns all concentrations of all chemical entities in the given cell section.
     *
     * @param cellSection The cell section.
     * @return All concentrations of all chemical entities.
     */
    public Map<ChemicalEntity, Quantity<MolarConcentration>> getAllConcentrationsForSection(CellSection cellSection) {
        return concentrationContainer.getAllConcentrationsForSection(cellSection);
    }

    /**
     * Gets the concentration of the given chemical entity.
     *
     * @param entity The chemical entity.
     * @return The concentration in mol/l.
     */
    public Quantity<MolarConcentration> getConcentration(ChemicalEntity entity) {
        return concentrationContainer.getConcentration(entity);
    }

    /**
     * Gets the concentration of the given chemical entity in the given compartment.
     *
     * @param entity The chemical entity.
     * @param cellSection The cell section.
     * @param quantity The quantity.
     */
    public void setAvailableConcentration(ChemicalEntity entity, CellSection cellSection, Quantity<MolarConcentration> quantity) {
        concentrationContainer.setAvailableConcentration(cellSection, entity, quantity);
    }

    /**
     * Returns the concentration of the given chemical entity in the given compartment.
     *
     * @param entity The chemical entity.
     * @param cellSection The cell section.
     * @return The concentration of the given chemical entity.
     */
    public Quantity<MolarConcentration> getAvailableConcentration(ChemicalEntity entity, CellSection cellSection) {
        return concentrationContainer.getAvailableConcentration(cellSection, entity);
    }

      /**
     * Adds a list of potential deltas to this node.
     *
     * @param potentialDeltas The potential deltas.
     */
    public void addPotentialDeltas(Collection<Delta> potentialDeltas) {
        updateManager.addPotentialDeltas(potentialDeltas);
    }

    /**
     * Adds a potential delta to this node.
     *
     * @param potentialDelta The potential delta.
     */
    public void addPotentialDelta(Delta potentialDelta) {
        updateManager.addPotentialDelta(potentialDelta);
    }

    /**
     * Clears the list of potential deltas. Usually done after {@link AutomatonNode#shiftDeltas()} or after rejecting a
     * time step.
     */
    public void clearPotentialDeltas() {
        updateManager.clearPotentialDeltas();
    }

    /**
     * Shifts the deltas from the potential delta list to the final delta list.
     */
    public void shiftDeltas() {
        updateManager.shiftDeltas();
    }

    /**
     * Applies all final deltas and clears the delta list.
     */
    public void applyDeltas() {
        updateManager.applyDeltas();
    }

    @Override
    public List<Delta> getPotentialSpatialDeltas() {
        return updateManager.getPotentialDeltas();
    }

    /**
     * Returns all referenced sections in this node.
     *
     * @return all referenced sections in this node.
     */
    public Set<CellSection> getAllReferencedSections() {
        return concentrationContainer.getAllReferencedSections();
    }

    /**
     * Returns all chemical entities referenced in this node.
     *
     * @return All chemical entities referenced in this node.
     */
    public Set<ChemicalEntity> getAllReferencedEntities() {
        return concentrationContainer.getAllReferencedEntities();
    }

    /**
     * Returns the node state.
     *
     * @return The node state.
     */
    public CellSectionState getState() {
        return state;
    }

    /**
     * Sets the state.
     *
     * @param state The node state.
     */
    public void setState(CellSectionState state) {
        this.state = state;
    }

    /**
     * Returns {@code true} if this node is observed.
     *
     * @return {@code true} if this node is observed.
     */
    public boolean isObserved() {
        return updateManager.isObserved();
    }

    /**
     * Sets the observed state of this node.
     *
     * @param isObserved {@code true} if this node is observed.
     */
    public void setObserved(boolean isObserved) {
        updateManager.setObserved(isObserved);
    }

    public boolean isConcentrationFixed() {
        return updateManager.isConcentrationFixed();
    }

    public void setConcentrationFixed(boolean concentrationFixed) {
        updateManager.setConcentrationFixed(concentrationFixed);
    }

    /**
     * Returns the primary cell section of this node (Membrane nodes may belong to multiple sections).
     *
     * @return The primary cell section of this node.
     */
    public CellSection getCellSection() {
        return cellSection;
    }

    /**
     * Sets the cell section of this node and references the node in the corresponding section.
     *
     * @param cellSection The cell section.
     */
    public void setCellSection(CellSection cellSection) {
        // TODO potentially all membrane stuff could be set here
        if (cellSection instanceof Membrane) {
            setState(MEMBRANE);
        }
        this.cellSection = cellSection;
        concentrationContainer = new SimpleConcentrationContainer(cellSection);
        updateManager.setConcentrations(concentrationContainer);
        this.cellSection.addNode(this);
    }

    @Override
    public String getStringIdentifier() {
        return getIdentifier().toString();
    }

    /**
     * Returns the {@link ConcentrationContainer} used by this node.
     *
     * @return The {@link ConcentrationContainer} used by this node.
     */
    public ConcentrationContainer getConcentrationContainer() {
        return concentrationContainer;
    }

    /**
     * Sets the {@link ConcentrationContainer} for this node.
     *
     * @param concentrationContainer The {@link ConcentrationContainer} for this node.
     */
    public void setConcentrationContainer(ConcentrationContainer concentrationContainer) {
        this.concentrationContainer = concentrationContainer;
        updateManager.setConcentrations(concentrationContainer);
    }

    public Polygon getSpatialRepresentation() {
        return spatialRepresentation;
    }

    public void setSpatialRepresentation(Polygon spatialRepresentation) {
        this.spatialRepresentation = spatialRepresentation;
    }

    public Map<Vesicle, Quantity<Area>> getAssociatedVesicles() {
        return associatedVesicles;
    }

    public void associateVesicle(Vesicle vesicle, Quantity<Area> associatedArea) {
        associatedVesicles.put(vesicle, associatedArea);
    }

    public void clearAssociatedVesicles() {
        associatedVesicles.clear();
    }

    @Override
    public String toString() {
        return "Node "+getIdentifier()+" ("+getState()+")";
    }

    @Override
    public AutomatonNode getCopy() {
        throw new UnsupportedOperationException("not implemented");
    }
}
