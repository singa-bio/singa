package de.bioforscher.singa.simulation.model.modules.concentration.specifity;

import de.bioforscher.singa.simulation.model.modules.concentration.ConcentrationBasedModule;
import de.bioforscher.singa.simulation.model.modules.concentration.ConcentrationDelta;
import de.bioforscher.singa.simulation.model.modules.concentration.ConcentrationDeltaIdentifier;
import de.bioforscher.singa.simulation.model.modules.concentration.FieldSupplier;
import de.bioforscher.singa.simulation.model.modules.concentration.functions.UpdatableDeltaFunction;
import de.bioforscher.singa.simulation.model.sections.ConcentrationContainer;

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
                Map<ConcentrationDeltaIdentifier, ConcentrationDelta> fullDeltas = deltaFunction.getFunction().apply(container);
                // for each resulting delta
                for (Map.Entry<ConcentrationDeltaIdentifier, ConcentrationDelta> entry : fullDeltas.entrySet()) {
                    ConcentrationDelta delta = entry.getValue();
                    ConcentrationDeltaIdentifier identifier = entry.getKey();
//                    supply().setCurrentUpdatable(identifier.getUpdatable());
//                    supply().setCurrentSubsection(identifier.getSubsection());
//                    supply().setCurrentEntity(identifier.getEntity());
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
