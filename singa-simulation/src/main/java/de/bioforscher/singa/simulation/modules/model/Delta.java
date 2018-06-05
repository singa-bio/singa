package de.bioforscher.singa.simulation.modules.model;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.features.quantities.MolarConcentration;
import de.bioforscher.singa.simulation.model.newsections.CellSubsection;

import javax.measure.Quantity;

/**
 * The delta object signifies the change that will be applied to the concentration of a specific {@link ChemicalEntity}
 * in a specific {@link CellSubsection}.
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
    private final CellSubsection cellSubsection;

    /**
     * The chemical entity.
     */
    private final ChemicalEntity chemicalEntity;

    /**
     * The change in concentration.
     */
    private Quantity<MolarConcentration> quantity;


    public Delta(Module module, CellSubsection cellSubsection, ChemicalEntity chemicalEntity, Quantity<MolarConcentration> quantity) {
        this.module = module;
        this.chemicalEntity = chemicalEntity;
        this.cellSubsection = cellSubsection;
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

    public CellSubsection getCellSubsection() {
        return cellSubsection;
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
    Delta multiply(double multiplicand) {
        quantity = quantity.multiply(multiplicand);
        return this;
    }

    @Override
    public String toString() {
        return module + " : " + cellSubsection.getIdentifier()+"-"+chemicalEntity.getIdentifier()+" = "+quantity;
    }
}
