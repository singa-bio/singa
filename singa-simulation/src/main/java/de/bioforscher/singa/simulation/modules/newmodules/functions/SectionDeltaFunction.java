package de.bioforscher.singa.simulation.modules.newmodules.functions;

import de.bioforscher.singa.simulation.model.newsections.ConcentrationContainer;
import de.bioforscher.singa.simulation.modules.newmodules.Delta;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @author cl
 */
public class SectionDeltaFunction extends AbstractDeltaFunction {

    private final Function<ConcentrationContainer, List<Delta>> function;

    public SectionDeltaFunction(Function<ConcentrationContainer, List<Delta>> function, Predicate<ConcentrationContainer> condition) {
        super(condition);
        this.function = function;
    }

    public Function<ConcentrationContainer, List<Delta>> getFunction() {
        return function;
    }

}
