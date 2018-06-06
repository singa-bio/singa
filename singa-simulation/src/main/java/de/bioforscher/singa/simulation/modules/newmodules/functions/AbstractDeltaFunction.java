package de.bioforscher.singa.simulation.modules.newmodules.functions;

import de.bioforscher.singa.simulation.model.newsections.ConcentrationContainer;

import java.util.function.Predicate;

/**
 * @author cl
 */
public abstract class AbstractDeltaFunction {

    private final Predicate<ConcentrationContainer> condition;

    public AbstractDeltaFunction(Predicate<ConcentrationContainer> condition) {
        this.condition = condition;
    }

    public Predicate<ConcentrationContainer> getCondition() {
        return condition;
    }

}
