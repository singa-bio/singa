package de.bioforscher.singa.simulation.modules.newmodules.functions;

import de.bioforscher.singa.simulation.model.newsections.ConcentrationContainer;
import de.bioforscher.singa.simulation.modules.model.Delta;
import de.bioforscher.singa.simulation.modules.model.DeltaIdentifier;

import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @author cl
 */
public class UpdatableDeltaFunction extends AbstractDeltaFunction {

    private final Function<ConcentrationContainer, Map<DeltaIdentifier, Delta>> function;

    public UpdatableDeltaFunction(Function<ConcentrationContainer, Map<DeltaIdentifier, Delta>> function, Predicate<ConcentrationContainer> condition) {
        super(condition);
        this.function = function;
    }

    public Function<ConcentrationContainer, Map<DeltaIdentifier, Delta>> getFunction() {
        return function;
    }

}
