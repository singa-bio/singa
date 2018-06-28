package de.bioforscher.singa.simulation.model.modules.concentration;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.simulation.model.sections.CellSubsection;
import de.bioforscher.singa.simulation.model.simulation.Updatable;

import java.util.HashMap;
import java.util.Map;

/**
 * @author cl
 */
public class FieldSupplier {

    private Updatable updatable;
    private CellSubsection subsection;
    private ChemicalEntity entity;
    private Map<ConcentrationDeltaIdentifier, ConcentrationDelta> fullDeltas;
    private Map<ConcentrationDeltaIdentifier, ConcentrationDelta> halfDeltas;
    private LocalError largestLocalError;
    private boolean isStrutCalculation;

    public FieldSupplier() {
        fullDeltas = new HashMap<>();
        halfDeltas = new HashMap<>();
        largestLocalError = LocalError.MINIMAL_EMPTY_ERROR;
    }

    public Updatable getCurrentUpdatable() {
        return updatable;
    }

    public void setCurrentUpdatable(Updatable updatable) {
        this.updatable = updatable;
    }

    public CellSubsection getCurrentSubsection() {
        return subsection;
    }

    public void setCurrentSubsection(CellSubsection subsection) {
        this.subsection = subsection;
    }

    public ChemicalEntity getCurrentEntity() {
        return entity;
    }

    public void setCurrentEntity(ChemicalEntity entity) {
        this.entity = entity;
    }

    public Map<ConcentrationDeltaIdentifier, ConcentrationDelta> getCurrentFullDeltas() {
        return fullDeltas;
    }

    public void setCurrentFullDeltas(Map<ConcentrationDeltaIdentifier, ConcentrationDelta> currentFullDeltas) {
        fullDeltas = currentFullDeltas;
    }

    public Map<ConcentrationDeltaIdentifier, ConcentrationDelta> getCurrentHalfDeltas() {
        return halfDeltas;
    }

    public void setCurrentHalfDeltas(Map<ConcentrationDeltaIdentifier, ConcentrationDelta> currentHalfDeltas) {
        halfDeltas = currentHalfDeltas;
    }

    public LocalError getLargestLocalError() {
        return largestLocalError;
    }

    public void setLargestLocalError(LocalError largestLocalError) {
        this.largestLocalError = largestLocalError;
    }

    public void resetError() {
        largestLocalError = LocalError.MINIMAL_EMPTY_ERROR;
    }

    public boolean isStrutCalculation() {
        return isStrutCalculation;
    }

    public void setStrutCalculation(boolean strutCalculation) {
        isStrutCalculation = strutCalculation;
    }

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
