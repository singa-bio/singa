package de.bioforscher.singa.simulation.modules.newmodules.scope;

import de.bioforscher.singa.features.quantities.MolarConcentration;
import de.bioforscher.singa.simulation.model.newsections.ConcentrationContainer;
import de.bioforscher.singa.simulation.modules.model.DeltaIdentifier;
import de.bioforscher.singa.simulation.modules.model.LocalError;
import de.bioforscher.singa.simulation.modules.model.Updatable;
import de.bioforscher.singa.simulation.modules.newmodules.Delta;
import de.bioforscher.singa.simulation.modules.newmodules.module.ConcentrationBasedModule;
import de.bioforscher.singa.simulation.modules.newmodules.module.FieldSupplier;
import de.bioforscher.singa.simulation.modules.newmodules.specifity.UpdateSpecificity;

import javax.measure.Quantity;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author cl
 */
public class SemiDependentUpdatable implements UpdateScope {

    private Map<Updatable, ConcentrationContainer> halfConcentrations;
    private ConcentrationBasedModule module;

    public SemiDependentUpdatable(ConcentrationBasedModule module) {
        this.module = module;
        halfConcentrations = new HashMap<>();
    }

    private FieldSupplier supply() {
        return module.getSupplier();
    }

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
    public LocalError processUpdatable(Updatable updatable) {
        // calculate full step deltas
        supply().setStrutCalculation(false);
        specify().processContainer(updatable.getConcentrationContainer());
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
        return supply().getLargestLocalError();
    }

    @Override
    public void clearPotentialDeltas(Updatable updatable) {
        module.getSimulation().getUpdatables().forEach(Updatable::clearPotentialDeltas);
    }

    private void determineHalfStepConcentrations() {
        halfConcentrations.clear();
        // for each full delta
        for (Map.Entry<DeltaIdentifier, Delta> entry : supply().getCurrentFullDeltas().entrySet()) {
            DeltaIdentifier identifier = entry.getKey();
            Delta fullDelta = entry.getValue();
            Updatable updatable = identifier.getUpdatable();
            ConcentrationContainer container;
            // check if container has been initialized
            if (halfConcentrations.containsKey(updatable)) {
                container = halfConcentrations.get(updatable);
            } else {
                container = updatable.getConcentrationContainer().fullCopy();
                halfConcentrations.put(updatable, container);
            }
            // get previous concentration
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
