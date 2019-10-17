package bio.singa.simulation.model.modules.concentration;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.simulation.model.sections.CellSubsection;
import bio.singa.simulation.model.simulation.Updatable;

import java.util.HashMap;
import java.util.Map;

/**
 * The field supplier manages all files required for different behaviours of a module.
 *
 * @author cl
 */
public class FieldSupplier {

    /**
     * The currently processed updatable.
     */
    private Updatable updatable;

    /**
     * The currently processed subsection.
     */
    private CellSubsection subsection;

    /**
     * The currently processed entity.
     */
    private ChemicalEntity entity;

    /**
     * The current full deltas.
     */
    private Map<ConcentrationDeltaIdentifier, ConcentrationDelta> fullDeltas;

    /**
     * The current half deltas.
     */
    private Map<ConcentrationDeltaIdentifier, ConcentrationDelta> halfDeltas;

    /**
     * The larges error as of yet.
     */
    private NumericalError largestLocalError;

    /**
     * Determines if the module currently processes full calculations of strut (half) calculations.
     */
    private boolean isStrutCalculation;

    /**
     * Creates a new Field Supplier.
     */
    FieldSupplier() {
        fullDeltas = new HashMap<>();
        halfDeltas = new HashMap<>();
        largestLocalError = NumericalError.MINIMAL_EMPTY_ERROR;
    }

    /**
     * Returns the current updatable.
     * @return The current updatable.
     */
    public Updatable getCurrentUpdatable() {
        return updatable;
    }

    /**
     * Sets the current updatable.
     * @param updatable The current updatable.
     */
    public void setCurrentUpdatable(Updatable updatable) {
        this.updatable = updatable;
    }

    /**
     * Returns the current subsection.
     * @return The current subsection.
     */
    public CellSubsection getCurrentSubsection() {
        return subsection;
    }

    /**
     * Sets the current subsection.
     * @param subsection The current subsection.
     */
    public void setCurrentSubsection(CellSubsection subsection) {
        this.subsection = subsection;
    }

    /**
     * Returns the current chemical entity.
     * @return The current chemical entity.
     */
    public ChemicalEntity getCurrentEntity() {
        return entity;
    }

    /**
     * Sets the current chemical entity.
     * @param entity The current chemical entity.
     */
    public void setCurrentEntity(ChemicalEntity entity) {
        this.entity = entity;
    }

    /**
     * Returns all currently available full deltas.
     * @return All currently available full deltas.
     */
    public Map<ConcentrationDeltaIdentifier, ConcentrationDelta> getCurrentFullDeltas() {
        return fullDeltas;
    }

    /**
     * Returns all currently available half deltas.
     * @return All currently available half deltas.
     */
    public Map<ConcentrationDeltaIdentifier, ConcentrationDelta> getCurrentHalfDeltas() {
        return halfDeltas;
    }

    /**
     * Returns the largest error as of yet.
     * @return the largest error as of yet.
     */
    public NumericalError getLargestLocalError() {
        return largestLocalError;
    }

    /**
     * Sets the largest error as of yet.
     * @param largestLocalError the largest error as of yet.
     */
    public void setLargestLocalError(NumericalError largestLocalError) {
        this.largestLocalError = largestLocalError;
    }

    /**
     * Resets the error to the minimal error.
     */
    public void resetError() {
        largestLocalError = NumericalError.MINIMAL_EMPTY_ERROR;
    }

    /**
     * Returns the current state of calculations.
     * @return True if currently strut points (half deltas) are calculated.
     */
    public boolean isStrutCalculation() {
        return isStrutCalculation;
    }

    /**
     * Sets the current state of calculation.
     * @param strutCalculation The current state of calculation.
     */
    public void setStrutCalculation(boolean strutCalculation) {
        isStrutCalculation = strutCalculation;
    }

    /**
     * Clears all full end half deltas.
     */
    public void clearDeltas() {
        fullDeltas.clear();
        halfDeltas.clear();
    }

    @Override
    public String toString() {
        return "FieldSupplier{" +
                "updatable=" + updatable +
                ", subsection=" + subsection +
                ", entity=" + entity +
                ", fullDeltas=" + fullDeltas +
                ", halfDeltas=" + halfDeltas +
                ", largestLocalError=" + largestLocalError +
                ", isStrutCalculation=" + isStrutCalculation +
                '}';
    }
}
