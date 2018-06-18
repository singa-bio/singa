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
public class DependentUpdate implements UpdateScope {

    private Map<Updatable, ConcentrationContainer> halfConcentrations;
    private ConcentrationBasedModule<?> module;

    public DependentUpdate(ConcentrationBasedModule<?> module) {
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
        for (DeltaIdentifier identifier : supply().getCurrentFullDeltas().keySet()) {
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
    public LocalError processUpdatable(Updatable updatable) {
        processAllUpdatables(module.getSimulation().getUpdatables());
        return supply().getLargestLocalError();
    }

    private void determineHalfStepConcentrations() {
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
