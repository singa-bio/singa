package bio.singa.simulation.model.modules.concentration;

import bio.singa.core.events.UpdateEventListener;
import bio.singa.simulation.model.graphs.AutomatonNode;
import bio.singa.simulation.model.modules.UpdateModule;
import bio.singa.simulation.model.sections.ConcentrationContainer;
import bio.singa.simulation.model.simulation.Updatable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

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
     * A list of potential deltas.
     */
    private final List<ConcentrationDelta> potentialDeltas;

    /**
     * The current concentrations.
     */
    private ConcentrationContainer currentConcentrations;

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
        potentialDeltas = new ArrayList<>();
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

    /**
     * Clears the list of potential deltas. Usually done after {@link AutomatonNode#shiftDeltas()} or after rejecting a
     * time step.
     */
    public void clearPotentialDeltas() {
        potentialDeltas.clear();
    }

    /**
     * Clears the list of potential deltas retaining updates from a specific module. Usually done after
     * {@link Updatable#shiftDeltas()} or after rejecting a time step.
     *
     * @param module The module.
     */
    public void clearPotentialDeltasBut(UpdateModule module) {
        potentialDeltas.removeIf(delta -> delta.getModule() != module);
    }

    /**
     * Shifts the deltas from the potential delta list to the final delta list.
     */
    public void shiftDeltas() {
        finalDeltas.addAll(potentialDeltas);
        if (!observed) {
            potentialDeltas.clear();
        }
    }

    /**
     * Applies all final deltas and clears the delta list.
     */
    public void applyDeltas() {
        if (!concentrationFixed) {
            for (ConcentrationDelta delta : finalDeltas) {
                double previousConcentration = currentConcentrations.get(delta.getCellSubsection(), delta.getChemicalEntity());
                double updatedConcentration = previousConcentration + delta.getValue();
                if (updatedConcentration < 0.0) {
                    // FIXME updated concentration should probably not be capped
                    // FIXME the the delta that resulted in the decrease probably had a corresponding increase
                    updatedConcentration = 0.0;
                }
                logger.trace("Setting c({}) in {} from {} to {} ", delta.getChemicalEntity().getIdentifier(), delta.getCellSubsection().getIdentifier(), previousConcentration, updatedConcentration);
                currentConcentrations.set(delta.getCellSubsection(), delta.getChemicalEntity(), updatedConcentration);
            }
        }
        finalDeltas.clear();
    }
}
