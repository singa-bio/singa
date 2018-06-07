package de.bioforscher.singa.simulation.modules.newmodules.functions;

import de.bioforscher.singa.simulation.model.newsections.ConcentrationContainer;
import de.bioforscher.singa.simulation.modules.newmodules.Delta;

import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @author cl
 */
public class EntityDeltaFunction extends AbstractDeltaFunction {

    private final Function<ConcentrationContainer, Delta> function;

    public EntityDeltaFunction(Function<ConcentrationContainer, Delta> function, Predicate<ConcentrationContainer> condition) {
        super(condition);
        this.function = function;
    }

    public Function<ConcentrationContainer, Delta> getFunction() {
        return function;
    }

}
