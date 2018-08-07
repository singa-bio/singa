package bio.singa.simulation.model.modules.concentration;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.features.quantities.MolarConcentration;
import bio.singa.simulation.model.modules.UpdateModule;
import bio.singa.simulation.model.sections.CellSubsection;

import javax.measure.Quantity;

/**
 * The delta object manages the change that will be applied to the concentration of a specific {@link ChemicalEntity} in
 * a {@link CellSubsection}.
 *
 * @author cl
 */
public class ConcentrationDelta {

    /**
     * The module, that calculated this delta.
     */
    private final UpdateModule module;

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

    /**
     * Creates a new concentration delta.
     * @param module The module the delta was calculated by.
     * @param cellSubsection The subsection the delta is applied to
     * @param chemicalEntity The chemical entity the delta is applied to.
     * @param quantity The actual quantity of the change.
     */
    public ConcentrationDelta(UpdateModule module, CellSubsection cellSubsection, ChemicalEntity chemicalEntity, Quantity<MolarConcentration> quantity) {
        this.module = module;
        this.chemicalEntity = chemicalEntity;
        this.cellSubsection = cellSubsection;
        this.quantity = quantity;
    }

    /**
     * Returns the module, that calculated this delta.
     *
     * @return The module, that calculated this delta.
     */
    public UpdateModule getModule() {
        return module;
    }

    /**
     * Returns the subsection.
     *
     * @return The subsection.
     */
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
     * Multiplies (modifies) this delta with the given multiplicand.
     *
     * @param multiplicand The scalar that the delta is multiplied with.
     * @return This multiplied delta.
     */
    public ConcentrationDelta multiply(double multiplicand) {
        quantity = quantity.multiply(multiplicand);
        return this;
    }

    @Override
    public String toString() {
        return module + " : " + cellSubsection.getIdentifier() + "-" + chemicalEntity.getIdentifier() + " = " + quantity;
    }
}
