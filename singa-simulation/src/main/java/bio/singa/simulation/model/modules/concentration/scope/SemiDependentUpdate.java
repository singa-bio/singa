package bio.singa.simulation.model.modules.concentration.scope;

import bio.singa.features.quantities.MolarConcentration;
import bio.singa.simulation.model.graphs.AutomatonNode;
import bio.singa.simulation.model.modules.concentration.ConcentrationBasedModule;
import bio.singa.simulation.model.modules.concentration.ConcentrationDelta;
import bio.singa.simulation.model.modules.concentration.ConcentrationDeltaIdentifier;
import bio.singa.simulation.model.modules.concentration.FieldSupplier;
import bio.singa.simulation.model.modules.concentration.imlementations.MembraneDiffusion;
import bio.singa.simulation.model.modules.concentration.specifity.UpdateSpecificity;
import bio.singa.simulation.model.modules.displacement.Vesicle;
import bio.singa.simulation.model.sections.ConcentrationContainer;
import bio.singa.simulation.model.simulation.Updatable;

import javax.measure.Quantity;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Semidependent Updatable {@link ConcentrationBasedModule}s require the integer state of a subset of updatables.
 * For example {@link MembraneDiffusion} might happen between a {@link Vesicle} and an {@link AutomatonNode}, therefore
 * at least two updatables are changed during calculation. Half step deltas can be calculated independently but, more
 * half deltas need to be stored to evaluate the error of the computation.
 *
 * @author cl
 */
public class SemiDependentUpdate implements UpdateScope {

    /**
     * The storage of the half concentrations.
     */
    private Map<Updatable, ConcentrationContainer> halfConcentrations;

    /**
     * The associated module.
     */
    private ConcentrationBasedModule module;

    /**
     * Initializes the update scope for the corresponding module.
     */
    public SemiDependentUpdate(ConcentrationBasedModule module) {
        this.module = module;
        halfConcentrations = new HashMap<>();
    }

    /**
     * Returns a object, managing shared properties of the module.
     *
     * @return The supplier.
     */
    private FieldSupplier supply() {
        return module.getSupplier();
    }

    /**
     * Returns the update specificity behaviour of the module, required for the actual computation of the updates.
     *
     * @return The update specificity behaviour.
     */
    private UpdateSpecificity specify() {
        return module.getSpecificity();
    }

    @Override
    public void processAllUpdatables(Collection<Updatable> updatables) {
        // for each updatable
        for (Updatable updatable : updatables) {
            if (module.getApplicationCondition().test(updatable)) {
                supply().setCurrentUpdatable(updatable);
                processUpdatable(updatable);
            }
        }
    }

    @Override
    public void processUpdatable(Updatable updatable) {
        // calculate full step deltas
        supply().setStrutCalculation(false);
        specify().processContainer(updatable.getConcentrationContainer());
        // if at least one delta has been determined
        if (!supply().getCurrentFullDeltas().isEmpty()) {
            // explicitly calculate half step concentrations
            determineHalfStepConcentrations();
            // calculate half step deltas
            supply().setStrutCalculation(true);
            specify().processContainer(getHalfStepConcentration(updatable));
            // set largest local error
            supply().setLargestLocalError(module.determineLargestLocalError());
            // clear used deltas
            supply().getCurrentFullDeltas().clear();
            supply().getCurrentHalfDeltas().clear();
        }
    }

    @Override
    public void clearPotentialDeltas(Updatable updatable) {
        module.getSimulation().getUpdatables().forEach(Updatable::clearPotentialConcentrationDeltas);
    }

    /**
     * Determines all half step concentrations for each calculated full delta.
     */
    private void determineHalfStepConcentrations() {
        // clean up previous values
        halfConcentrations.clear();
        // for each full delta
        for (Map.Entry<ConcentrationDeltaIdentifier, ConcentrationDelta> entry : supply().getCurrentFullDeltas().entrySet()) {
            // get required values
            final ConcentrationDeltaIdentifier identifier = entry.getKey();
            final ConcentrationDelta fullDelta = entry.getValue();
            final Updatable updatable = identifier.getUpdatable();
            ConcentrationContainer container;
            // check if container has already been initialized
            if (halfConcentrations.containsKey(updatable)) {
                container = halfConcentrations.get(updatable);
            } else {
                container = updatable.getConcentrationContainer().fullCopy();
                halfConcentrations.put(updatable, container);
            }
            // get full concentration
            Quantity<MolarConcentration> fullConcentration = container.get(identifier.getSubsection(), identifier.getEntity());
            // add half of the full delta
            Quantity<MolarConcentration> halfStepConcentration = fullConcentration.add(fullDelta.getQuantity().multiply(0.5));
            // update concentration
            container.set(identifier.getSubsection(), identifier.getEntity(), halfStepConcentration);
        }
    }

    @Override
    public ConcentrationContainer getHalfStepConcentration(Updatable updatable) {
        ConcentrationContainer container = halfConcentrations.get(updatable);
        if (container == null) {
            throw new IllegalStateException("No half concentration container has been defined for " + updatable + ".");
        }
        return container;
    }

}
