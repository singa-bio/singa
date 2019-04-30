package bio.singa.simulation.model.modules.concentration;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.core.events.UpdateEventListener;
import bio.singa.features.quantities.MolarConcentration;
import bio.singa.simulation.model.modules.UpdateModule;
import bio.singa.simulation.model.sections.CellSubsection;
import bio.singa.simulation.model.sections.ConcentrationContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The ConcentrationDeltaManager handles current concentrations of an updatable and the updates to those
 * concentrations that should be applied during simulation.
 *
 * @author cl
 */
public class ConcentrationDeltaManager {

    /**
     * The logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(ConcentrationDeltaManager.class);

    /**
     * The current concentration, this is modified during calculations and might not be the actual value.
     */
    private ConcentrationContainer currentConcentrations;

    /**
     * A copy of the original concentrations used to calculate the full step concentrations for the evaluation of the
     * global error.
     */
    private ConcentrationContainer interimConcentrations;

    /**
     * A copy of the concentrations at the start of the global error evaluation to revert to if recalculations are
     * required.
     */
    private ConcentrationContainer originalConcentrations;

    /**
     * A list of potential deltas.
     */
    private final List<ConcentrationDelta> potentialDeltas;


    /**
     * Deltas that are to be applied to the node.
     */
    private final List<ConcentrationDelta> finalDeltas;

    /**
     * A flag signifying if this node is observed.
     */
    private boolean observed;

    /**
     * A flag signifying if this node has a fixed concentration.
     */
    private boolean concentrationFixed;

    /**
     * Creates a new Concentration Delta Manager.
     *
     * @param initialConcentrations The initial concentrations.
     */
    public ConcentrationDeltaManager(ConcentrationContainer initialConcentrations) {
        finalDeltas = new ArrayList<>();
        potentialDeltas = Collections.synchronizedList(new ArrayList<>());
        observed = false;
        concentrationFixed = false;
        currentConcentrations = initialConcentrations;
    }

    /**
     * Returns the current concentration container.
     *
     * @return The current concentration container.
     */
    public ConcentrationContainer getConcentrationContainer() {
        return currentConcentrations;
    }

    /**
     * Sets the current concentration container.
     *
     * @param concentrations The current concentration container.
     */
    public void setConcentrationContainer(ConcentrationContainer concentrations) {
        currentConcentrations = concentrations;
    }

    public ConcentrationContainer getOriginalConcentrations() {
        return originalConcentrations;
    }

    /**
     * Returns true if the concentrations are observed.
     *
     * @return True if the concentrations are observed.
     */
    public boolean isObserved() {
        return observed;
    }

    /**
     * Sets the concentrations to be observed, this additional requires seting up a {@link UpdateEventListener}.
     *
     * @param observed True if th concentrations should be observed.
     */
    public void setObserved(boolean observed) {
        this.observed = observed;
    }

    /**
     * Returns true if the concentration is fixed - no deltas are applied.
     *
     * @return true if the concentration is fixed - no deltas are applied.
     */
    public boolean isConcentrationFixed() {
        return concentrationFixed;
    }

    /**
     * Sets the concentration to be fixed - no deltas will be applied.
     *
     * @param concentrationFixed True if the concentration should be fixed.
     */
    public void setConcentrationFixed(boolean concentrationFixed) {
        this.concentrationFixed = concentrationFixed;
    }

    public boolean hasDeltas() {
        return !getFinalDeltas().isEmpty();
    }

    public void setInterimAndUpdateCurrentConcentrations() {
        boolean repeat;
        do {
            repeat = false;
            currentConcentrations = originalConcentrations.fullCopy();
            interimConcentrations = originalConcentrations.fullCopy();
            for (ConcentrationDelta delta : potentialDeltas) {
                // if delta would add a new entity do not consider it during stability evaluation
                if (currentConcentrations.get(delta.getCellSubsection(), delta.getChemicalEntity()) == 0.0) {
                    continue;
                }
                double updatedHalfConcentration = currentConcentrations.get(delta.getCellSubsection(), delta.getChemicalEntity()) + delta.getValue() * 0.5;
                double updatedFullConcentration = interimConcentrations.get(delta.getCellSubsection(), delta.getChemicalEntity()) + delta.getValue();
                if (updatedFullConcentration < 0.0) {
                    capDeltas(delta.getModule(), delta);
                    repeat = true;
                    break;
                }
                currentConcentrations.set(delta.getCellSubsection(), delta.getChemicalEntity(), updatedHalfConcentration);
                interimConcentrations.set(delta.getCellSubsection(), delta.getChemicalEntity(), updatedFullConcentration);
            }
        } while (repeat);
        potentialDeltas.clear();
    }

    public void determineComparisionConcentrations() {
        boolean repeat;
        do {
            repeat = false;
            currentConcentrations = originalConcentrations.fullCopy();
            for (ConcentrationDelta delta : potentialDeltas) {
                // add to original (0) concentrations full delta (1) = 1
                double updatedConcentration = currentConcentrations.get(delta.getCellSubsection(), delta.getChemicalEntity()) + delta.getValue();
                if (updatedConcentration < 0.0) {
                    // cap deltas such that the minimal value of the delta can be zero
                    capDeltas(delta.getModule(), delta);
                    repeat = true;
                    break;
                }
                currentConcentrations.set(delta.getCellSubsection(), delta.getChemicalEntity(), updatedConcentration);
            }
        } while (repeat);
    }

    private void capDeltas(UpdateModule module, ConcentrationDelta delta) {
        double remainingConcentration = currentConcentrations.get(delta.getCellSubsection(), delta.getChemicalEntity());
        List<ConcentrationDelta> affectedDeltas = potentialDeltas.stream()
                .filter(element -> element.getModule().equals(module))
                .collect(Collectors.toList());
        double deltaValue = delta.getValue();
        for (ConcentrationDelta affectedDelta : affectedDeltas) {
            if (affectedDelta.equals(delta)) {
                delta.setValue(-remainingConcentration);
            } else {
                // determine relationship
                double factor = affectedDelta.getValue() / deltaValue;
                affectedDelta.setValue(Math.signum(affectedDelta.getValue()) * remainingConcentration * factor);
            }
        }

    }

    public NumericalError determineGlobalNumericalError() {
        double largestError = 0.0;
        ChemicalEntity errorEntity = null;
        for (ChemicalEntity entity : currentConcentrations.getReferencedEntities()) {
            for (CellSubsection subsection : currentConcentrations.getReferencedSubsections()) {
                double interimConcentration = interimConcentrations.get(subsection, entity);
                double currentConcentration = currentConcentrations.get(subsection, entity);
                if (currentConcentration != 0.0 && interimConcentration != 0.0) {
                    double globalError = Math.abs(1 - (interimConcentration / currentConcentration));
                    if (globalError > largestError) {
                        largestError = globalError;
                        errorEntity = entity;
                    }
                }
            }
        }
        return new NumericalError(null, errorEntity, largestError);
    }


    /**
     * Returns all deltas that are going to be applied to this node.
     *
     * @return All deltas that are going to be applied to this node.
     */
    public List<ConcentrationDelta> getFinalDeltas() {
        return finalDeltas;
    }

    /**
     * Returns all potential deltas that should be applied.
     *
     * @return All potential deltas that should be applied.
     */
    public List<ConcentrationDelta> getPotentialDeltas() {
        return potentialDeltas;
    }

    /**
     * Adds a potential delta to this updatable.
     *
     * @param potentialDelta The potential delta.
     */
    public void addPotentialDelta(ConcentrationDelta potentialDelta) {
        potentialDeltas.add(potentialDelta);
    }

    public void backupConcentrations() {
        originalConcentrations = currentConcentrations.fullCopy();
    }

    public void revertToOriginalConcentrations() {
        currentConcentrations = originalConcentrations.fullCopy();
    }

    /**
     * Clears the list of potential deltas. Usually done after {@link ConcentrationDeltaManager#shiftDeltas()} or after rejecting a
     * time step.
     */
    public void clearPotentialDeltas() {
        potentialDeltas.clear();
    }

    /**
     * Clears the list of potential deltas retaining updates from a specific module. Usually done after
     * {@link ConcentrationDeltaManager#shiftDeltas()} or after rejecting a time step.
     *
     * @param module The module.
     */
    public void clearPotentialDeltasBut(UpdateModule module) {
        synchronized (potentialDeltas) {
            potentialDeltas.removeIf(delta -> delta.getModule() != module);
        }
    }

    /**
     * Shifts the deltas from the potential delta list to the final delta list.
     */
    public void shiftDeltas() {
        synchronized (potentialDeltas) {
            finalDeltas.addAll(potentialDeltas);
        }
        if (!observed) {
            potentialDeltas.clear();
        }
    }

    /**
     * Applies all final deltas and clears the delta list.
     */
    public void applyDeltas() {
        currentConcentrations = originalConcentrations;
        interimConcentrations = originalConcentrations;
        for (ConcentrationDelta delta : finalDeltas) {
            // it may happen that concentrations are calculated as strut points that have no representations in the
            // original concentrations and therefore non existent entities would be removed
            double previousConcentration = currentConcentrations.get(delta.getCellSubsection(), delta.getChemicalEntity());
            double updatedConcentration = previousConcentration + delta.getValue();
            if (updatedConcentration < 0.0) {
                if (MolarConcentration.concentrationToMolecules(Math.abs(delta.getValue())).getValue().doubleValue() < 0.1) {
                    logger.warn("Updates for {} have reached a cutoff value where less than a 1/10 of a molecule would remain, setting concentration to 0.", delta.getChemicalEntity());
                    updatedConcentration = 0.0;
                }
            }
            logger.trace("Setting {} in {} from {} to {} ", delta.getChemicalEntity(), delta.getCellSubsection().getIdentifier(), previousConcentration, updatedConcentration);
            currentConcentrations.set(delta.getCellSubsection(), delta.getChemicalEntity(), updatedConcentration);
        }
        finalDeltas.clear();
    }


}
