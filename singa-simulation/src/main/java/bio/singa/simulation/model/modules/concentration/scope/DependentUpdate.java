package bio.singa.simulation.model.modules.concentration.scope;

import bio.singa.features.quantities.MolarConcentration;
import bio.singa.simulation.model.modules.concentration.*;
import bio.singa.simulation.model.modules.concentration.imlementations.Diffusion;
import bio.singa.simulation.model.modules.concentration.specifity.UpdateSpecificity;
import bio.singa.simulation.model.sections.ConcentrationContainer;
import bio.singa.simulation.model.simulation.Updatable;

import javax.measure.Quantity;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Dependent Updatable {@link ConcentrationBasedModule}s require an integer state of basically all updatables in a
 * simulation. First all updates for all updatables are calculated. Afterwards all half step concentrations are
 * determined and further all errors are calculated, looking for the {@link Updatable} with the largest
 * {@link LocalError} (e.g. {@link Diffusion}).
 *
 * @author cl
 */
public class DependentUpdate implements UpdateScope {

    /**
     * The storage of the half concentrations.
     */
    private Map<Updatable, ConcentrationContainer> halfConcentrations;

    /**
     * The associated module.
     */
    private ConcentrationBasedModule<?> module;

    /**
     * Initializes the update scope for the corresponding module.
     * @param module The module.
     */
    public DependentUpdate(ConcentrationBasedModule<?> module) {
        this.module = module;
        halfConcentrations = new HashMap<>();
    }

    /**
     * Returns a object, managing shared properties of the module.
     * @return The supplier.
     */
    private FieldSupplier supply() {
        return module.getSupplier();
    }

    /**
     * Returns the update specificity behaviour of the module, required for the actual computation of the updates.
     * @return The update specificity behaviour.
     */
    private UpdateSpecificity specify() {
        return module.getSpecificity();
    }

    @Override
    public void processAllUpdatables(Collection<Updatable> updatables) {
        // calculate all full updates first
        supply().setStrutCalculation(false);
        for (Updatable updatable : updatables) {
            if (module.getApplicationCondition().test(updatable)) {
                supply().setCurrentUpdatable(updatable);
                specify().processContainer(updatable.getConcentrationContainer());
            }
        }
        // explicitly calculate half step concentrations
        determineHalfStepConcentrations();
        supply().setStrutCalculation(true);
        for (ConcentrationDeltaIdentifier identifier : supply().getCurrentFullDeltas().keySet()) {
            supply().setCurrentUpdatable(identifier.getUpdatable());
            specify().processContainer(getHalfStepConcentration(identifier.getUpdatable()));
        }
        // set largest local error
        supply().setLargestLocalError(module.determineLargestLocalError());
        // clear used deltas
        supply().getCurrentFullDeltas().clear();
        supply().getCurrentHalfDeltas().clear();
    }

    @Override
    public void processUpdatable(Updatable updatable) {
        processAllUpdatables(module.getSimulation().getUpdatables());
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
            // check if container has been initialized
            if (halfConcentrations.containsKey(updatable)) {
                container = halfConcentrations.get(updatable);
            } else {
                container = updatable.getConcentrationContainer().fullCopy();
                halfConcentrations.put(updatable, container);
            }
            // get full concentration
            Quantity<MolarConcentration> fullConcentration = updatable.getConcentration(identifier.getSubsection(), identifier.getEntity());
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
