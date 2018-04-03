package de.bioforscher.singa.simulation.modules.model;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.features.parameters.EnvironmentalParameters;
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
     * The module, that calculated this delta.
     */
    private final Module module;

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
     * Creates a new delta.
     *
     * @param module The module.
     * @param cellSection The cell section.
     * @param chemicalEntity The chemical entity.
     * @param quantity The change in concentration.
     */
    public Delta(Module module, CellSection cellSection, ChemicalEntity chemicalEntity, Quantity<MolarConcentration> quantity) {
        this.module = module;
        this.chemicalEntity = chemicalEntity;
        this.cellSection = cellSection;
        this.quantity = quantity;
    }

    /**
     * Reutns the module, that calculated this delta.
     *
     * @return The module, that calculated this delta.
     */
    public Module getModule() {
        return module;
    }

    /**
     * Returns the cell section.
     *
     * @return The cell section.
     */
    public CellSection getCellSection() {
        return cellSection;
    }

    /**
     * Returns the chemical entity.
     *
     * @return The chemical entity.
     */
    public ChemicalEntity getChemicalEntity() {
        return chemicalEntity;
    }

    /**
     * Returns the quantity of the change.
     *
     * @return The quantity of the change.
     */
    public Quantity<MolarConcentration> getQuantity() {
        return quantity;
    }

    /**
     * Multiplies (modifies) this delta .
     *
     * @param multiplicand The scalar that the delta is multiplied with.
     * @return This multiplied delta.
     */
    public Delta multiply(double multiplicand) {
        quantity = quantity.multiply(multiplicand);
        return this;
    }

    @Override
    public String toString() {
        return module + " : " + cellSection.getIdentifier()+"-"+chemicalEntity.getIdentifier()+" = "+EnvironmentalParameters.DELTA_FORMATTER.format(quantity);
    }
}
