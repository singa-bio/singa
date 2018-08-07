package bio.singa.simulation.model.modules.concentration.specifity;

import bio.singa.simulation.model.modules.concentration.ConcentrationBasedModule;
import bio.singa.simulation.model.modules.concentration.ConcentrationDelta;
import bio.singa.simulation.model.modules.concentration.ConcentrationDeltaIdentifier;
import bio.singa.simulation.model.modules.concentration.functions.AbstractDeltaFunction;
import bio.singa.simulation.model.modules.concentration.functions.UpdatableDeltaFunction;
import bio.singa.simulation.model.sections.ConcentrationContainer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Updatable specific {@link ConcentrationBasedModule}s calculate their {@link AbstractDeltaFunction} for every
 * updatable.
 *
 * @author cl
 */
public class UpdatableSpecific implements UpdateSpecificity<UpdatableDeltaFunction> {

    /**
     * The delta functions to be calculated.
     */
    private List<UpdatableDeltaFunction> deltaFunctions;

    /**
     * The associated module.
     */
    private ConcentrationBasedModule module;

    /**
     * Initializes the update specificity for the corresponding module.
     * @param module The module.
     */
    public UpdatableSpecific(ConcentrationBasedModule module) {
        this.module = module;
        deltaFunctions = new ArrayList<>();
    }

    @Override
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
