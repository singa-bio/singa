package de.bioforscher.singa.simulation.model.modules.concentration.functions;

import de.bioforscher.singa.simulation.model.modules.concentration.ConcentrationDelta;
import de.bioforscher.singa.simulation.model.modules.concentration.ConcentrationDeltaIdentifier;
import de.bioforscher.singa.simulation.model.sections.ConcentrationContainer;

import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @author cl
 */
public class UpdatableDeltaFunction extends AbstractDeltaFunction {

    private final Function<ConcentrationContainer, Map<ConcentrationDeltaIdentifier, ConcentrationDelta>> function;

    public UpdatableDeltaFunction(Function<ConcentrationContainer, Map<ConcentrationDeltaIdentifier, ConcentrationDelta>> function, Predicate<ConcentrationContainer> condition) {
        super(condition);
        this.function = function;
    }

    public Function<ConcentrationContainer, Map<ConcentrationDeltaIdentifier, ConcentrationDelta>> getFunction() {
        return function;
    }

}
