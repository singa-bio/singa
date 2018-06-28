package de.bioforscher.singa.simulation.model.modules.concentration.functions;

import de.bioforscher.singa.simulation.model.modules.concentration.ConcentrationDelta;
import de.bioforscher.singa.simulation.model.sections.ConcentrationContainer;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @author cl
 */
public class SectionDeltaFunction extends AbstractDeltaFunction {

    private final Function<ConcentrationContainer, List<ConcentrationDelta>> function;

    public SectionDeltaFunction(Function<ConcentrationContainer, List<ConcentrationDelta>> function, Predicate<ConcentrationContainer> condition) {
        super(condition);
        this.function = function;
    }

    public Function<ConcentrationContainer, List<ConcentrationDelta>> getFunction() {
        return function;
    }

}
