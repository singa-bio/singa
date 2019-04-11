package bio.singa.simulation.model.modules.concentration;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.simulation.model.modules.UpdateModule;
import bio.singa.simulation.model.sections.CellSubsection;

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
    private double value;

    /**
     * Creates a new concentration delta.
     * @param module The module the delta was calculated by.
     * @param cellSubsection The subsection the delta is applied to
     * @param chemicalEntity The chemical entity the delta is applied to.
     * @param value The actual value of the change.
     */
    public ConcentrationDelta(UpdateModule module, CellSubsection cellSubsection, ChemicalEntity chemicalEntity, double value) {
        this.module = module;
        this.chemicalEntity = chemicalEntity;
        this.cellSubsection = cellSubsection;
        this.value = value;
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
     * Returns the value of the change.
     *
     * @return The value of the change.
     */
    public double getValue() {
        return value;
    }


    public void setValue(double value) {
        this.value = value;
    }

    /**
     * Multiplies (modifies) this delta with the given multiplicand.
     *
     * @param multiplicand The scalar that the delta is multiplied with.
     * @return This multiplied delta.
     */
    public ConcentrationDelta multiply(double multiplicand) {
        value = value * multiplicand;
        return this;
    }

    public ConcentrationDelta add(double summand) {
        value = value + summand;
        return this;
    }

    @Override
    public String toString() {
        return module + " : " + cellSubsection.getIdentifier() + "-" + chemicalEntity.getIdentifier() + " = " + value;
    }
}
