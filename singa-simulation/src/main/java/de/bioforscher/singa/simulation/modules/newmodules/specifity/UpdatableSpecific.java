package de.bioforscher.singa.simulation.modules.newmodules.specifity;

import de.bioforscher.singa.simulation.model.newsections.ConcentrationContainer;
import de.bioforscher.singa.simulation.modules.model.DeltaIdentifier;
import de.bioforscher.singa.simulation.modules.newmodules.Delta;
import de.bioforscher.singa.simulation.modules.newmodules.functions.UpdatableDeltaFunction;
import de.bioforscher.singa.simulation.modules.newmodules.module.ConcentrationBasedModule;
import de.bioforscher.singa.simulation.modules.newmodules.module.FieldSupplier;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * defines delta functions
 *
 * @author cl
 */
public class UpdatableSpecific implements UpdateSpecificity<UpdatableDeltaFunction> {

    private List<UpdatableDeltaFunction> deltaFunctions;
    private ConcentrationBasedModule module;

    public UpdatableSpecific(ConcentrationBasedModule module) {
        this.module = module;
        deltaFunctions = new ArrayList<>();
    }

    private FieldSupplier supply() {
        return module.getSupplier();
    }

    public void processContainer(ConcentrationContainer container) {
        determineDeltas(container);
    }

    @Override
    public void determineDeltas(ConcentrationContainer container) {
        // for each designated function
        for (UpdatableDeltaFunction deltaFunction : deltaFunctions) {
            // test condition
            if (deltaFunction.getCondition().test(container)) {
                // apply function
                Map<DeltaIdentifier, Delta> fullDeltas = deltaFunction.getFunction().apply(container);
                // for each resulting delta
                for (Map.Entry<DeltaIdentifier, Delta> entry : fullDeltas.entrySet()) {
                    Delta delta = entry.getValue();
                    DeltaIdentifier identifier = entry.getKey();
                    supply().setCurrentUpdatable(identifier.getUpdatable());
                    supply().setCurrentSubsection(identifier.getSection());
                    supply().setCurrentEntity(identifier.getEntity());
                    if (module.deltaIsValid(delta)) {
                        module.handleDelta(identifier, delta);
                    }
                }
            }
        }
    }

    @Override
    public void addDeltaFunction(UpdatableDeltaFunction updatableDeltaFunction) {
        deltaFunctions.add(updatableDeltaFunction);
    }
}
