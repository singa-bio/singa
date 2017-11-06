package de.bioforscher.singa.simulation.model.concentrations;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.features.quantities.MolarConcentration;
import de.bioforscher.singa.simulation.model.compartments.CellSection;

import javax.measure.Quantity;

/**
 * The delta object signifies the change that will be applied to the concentration of a specific {@link ChemicalEntity}
 * in a specific {@link CellSection}.
 *
 * @author cl
 */
public class Delta {

    /**
     * The cell section.
     */
    private final CellSection cellSection;

    /**
     * The chemical entity.
     */
    private final ChemicalEntity chemicalEntity;

    /**
     * The change in concentration.
     */
    private Quantity<MolarConcentration> quantity;

    /**
     * Creates a new delta for a specific {@link ChemicalEntity} in a specific {@link CellSection}.
     * @param cellSection The cell section.
     * @param chemicalEntity The chemical entity.
     * @param quantity The change in concentration.
     */
    public Delta(CellSection cellSection, ChemicalEntity chemicalEntity, Quantity<MolarConcentration> quantity) {
        this.chemicalEntity = chemicalEntity;
        this.cellSection = cellSection;
        this.quantity = quantity;
    }

    /**
     * Returns the cell section.
     * @return The cell section.
     */
    public CellSection getCellSection() {
        return this.cellSection;
    }

    /**
     * Returns the chemical entity.
     * @return The chemical entity.
     */
    public ChemicalEntity getChemicalEntity() {
        return this.chemicalEntity;
    }

    /**
     * Returns the quantity of the change.
     * @return The quantity of the change.
     */
    public Quantity<MolarConcentration> getQuantity() {
        return this.quantity;
    }

    /**
     * Multiplies (modifies) this delta .
     * @param multiplicand The scalar that the delta is multiplied with.
     * @return This multiplied delta.
     */
    public Delta multiply(double multiplicand) {
        this.quantity = this.quantity.multiply(multiplicand);
        return this;
    }

    /**
     * Merges (adds) two deltas an returns a new delta.
     * @param anotherDelta The other delta.
     * @return A new delta with the accumulated changes.
     */
    public Delta merge(Delta anotherDelta) {
        return new Delta(this.cellSection, this.chemicalEntity, this.quantity.add(anotherDelta.getQuantity()));
    }

    @Override
    public String toString() {
        return "Delta{" +
                "cellSection=" + cellSection.getIdentifier() +
                ", entity=" + chemicalEntity.getIdentifier() +
                ", quantity=" + quantity +
                '}';
    }
}
