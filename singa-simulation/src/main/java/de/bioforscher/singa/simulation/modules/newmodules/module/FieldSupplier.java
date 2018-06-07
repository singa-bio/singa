package de.bioforscher.singa.simulation.modules.newmodules.module;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.simulation.model.newsections.CellSubsection;
import de.bioforscher.singa.simulation.modules.model.DeltaIdentifier;
import de.bioforscher.singa.simulation.modules.model.LocalError;
import de.bioforscher.singa.simulation.modules.model.Updatable;
import de.bioforscher.singa.simulation.modules.newmodules.Delta;

import java.util.HashMap;
import java.util.Map;

/**
 * @author cl
 */
public class FieldSupplier {

    private Updatable updatable;
    private CellSubsection subsection;
    private ChemicalEntity entity;
    private Map<DeltaIdentifier, Delta> fullDeltas;
    private Map<DeltaIdentifier, Delta> halfDeltas;
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

    public Map<DeltaIdentifier, Delta> getCurrentFullDeltas() {
        return fullDeltas;
    }

    public void setCurrentFullDeltas(Map<DeltaIdentifier, Delta> currentFullDeltas) {
        fullDeltas = currentFullDeltas;
    }

    public Map<DeltaIdentifier, Delta> getCurrentHalfDeltas() {
        return halfDeltas;
    }

    public void setCurrentHalfDeltas(Map<DeltaIdentifier, Delta> currentHalfDeltas) {
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

}
