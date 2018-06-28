package de.bioforscher.singa.simulation.model.modules.concentration.functions;

import de.bioforscher.singa.simulation.model.modules.concentration.ConcentrationDelta;
import de.bioforscher.singa.simulation.model.sections.ConcentrationContainer;

import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @author cl
 */
public class EntityDeltaFunction extends AbstractDeltaFunction {

    private final Function<ConcentrationContainer, ConcentrationDelta> function;

    public EntityDeltaFunction(Function<ConcentrationContainer, ConcentrationDelta> function, Predicate<ConcentrationContainer> condition) {
        super(condition);
        this.function = function;
    }

    public Function<ConcentrationContainer, ConcentrationDelta> getFunction() {
        return function;
    }

}
