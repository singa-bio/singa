package de.bioforscher.singa.simulation.model.graphs;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.features.quantities.MolarConcentration;
import de.bioforscher.singa.mathematics.geometry.model.Polygon;
import de.bioforscher.singa.mathematics.graphs.model.AbstractNode;
import de.bioforscher.singa.mathematics.topology.grids.rectangular.RectangularCoordinate;
import de.bioforscher.singa.mathematics.vectors.Vector2D;
import de.bioforscher.singa.simulation.model.newsections.CellRegion;
import de.bioforscher.singa.simulation.model.newsections.CellSubsection;
import de.bioforscher.singa.simulation.model.newsections.ConcentrationContainer;
import de.bioforscher.singa.simulation.modules.model.SimpleUpdateManager;
import de.bioforscher.singa.simulation.modules.model.Updatable;
import de.bioforscher.singa.simulation.modules.newmodules.Delta;

import javax.measure.Quantity;
import java.util.List;
import java.util.Set;

/**
 * A node of the {@link AutomatonGraph}. Contains the concentrations of {@link ChemicalEntity}s
 * in a {@link ConcentrationContainer} and other attributes. Each node holds references to its neighbour nodes.
 *
 * @author cl
 */
public class AutomatonNode extends AbstractNode<AutomatonNode, Vector2D, RectangularCoordinate> implements Updatable {


    private CellRegion cellRegion;

    private SimpleUpdateManager updateManager;

    private Polygon spatialRepresentation;

    public AutomatonNode(RectangularCoordinate identifier) {
        super(identifier);
        cellRegion = CellRegion.CYTOSOL_A;
        updateManager = new SimpleUpdateManager(cellRegion.setUpConcentrationContainer());
    }

    public AutomatonNode(int column, int row) {
        this(new RectangularCoordinate(column, row));
    }

    /**
     * Gets the concentration of the given chemical entity in the given compartment.
     *
     * @param entity The chemical entity.
     * @param section The cell section.
     * @param quantity The quantity.
     */
    public void setAvailableConcentration(CellSubsection section, ChemicalEntity entity, Quantity<MolarConcentration> quantity) {
        updateManager.getConcentrationContainer().set(section, entity, quantity);
    }

    /**
     * Returns the concentration of the given chemical entity in the given compartment.
     *
     * @param entity The chemical entity.
     * @param section The cell section.
     * @return The concentration of the given chemical entity.
     */
    @Override
    public Quantity<MolarConcentration> getConcentration(CellSubsection section, ChemicalEntity entity) {
        return updateManager.getConcentrationContainer().get(section, entity);
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
    public Set<CellSubsection> getAllReferencedSections() {
        return updateManager.getConcentrationContainer().getReferencedSubSections();
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

    @Override
    public CellRegion getCellRegion() {
        return cellRegion;
    }

    public void setCellRegion(CellRegion cellRegion) {
        this.cellRegion = cellRegion;
        updateManager.setConcentrationContainer(cellRegion.setUpConcentrationContainer());
    }

    @Override
    public String getStringIdentifier() {
        return "Node "+getIdentifier().toString();
    }

    /**
     * Returns the {@link ConcentrationContainer} used by this node.
     *
     * @return The {@link ConcentrationContainer} used by this node.
     */
    public ConcentrationContainer getConcentrationContainer() {
        return updateManager.getConcentrationContainer();
    }

    /**
     * Sets the {@link ConcentrationContainer} for this node.
     *
     * @param concentrationContainer The {@link ConcentrationContainer} for this node.
     */
    public void setConcentrationContainer(ConcentrationContainer concentrationContainer) {
        updateManager.setConcentrationContainer(concentrationContainer);
    }

    public Polygon getSpatialRepresentation() {
        return spatialRepresentation;
    }

    public void setSpatialRepresentation(Polygon spatialRepresentation) {
        this.spatialRepresentation = spatialRepresentation;
    }

    @Override
    public String toString() {
        return "Node "+getIdentifier()+" ("+cellRegion+")";
    }

    @Override
    public AutomatonNode getCopy() {
        throw new UnsupportedOperationException("not implemented");
    }
}
