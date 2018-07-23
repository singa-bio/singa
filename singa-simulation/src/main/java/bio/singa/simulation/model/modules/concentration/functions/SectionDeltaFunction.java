package bio.singa.simulation.model.modules.concentration.functions;

import bio.singa.simulation.model.modules.concentration.ConcentrationBasedModule;
import bio.singa.simulation.model.modules.concentration.ConcentrationDelta;
import bio.singa.simulation.model.modules.concentration.imlementations.Reaction;
import bio.singa.simulation.model.modules.concentration.specifity.SectionSpecific;
import bio.singa.simulation.model.sections.CellSubsection;
import bio.singa.simulation.model.sections.ConcentrationContainer;
import bio.singa.simulation.model.simulation.Updatable;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Section delta functions are {@link AbstractDeltaFunction}s which return a list of concentration deltas.
 * Mostly used for {@link SectionSpecific} {@link ConcentrationBasedModule}s, where the function is applied once per
 * {@link CellSubsection} in every {@link Updatable}. Each calculation returns the change of a specified set of
 * entities (e.g. for {@link Reaction}s).
 *
 * @author cl
 */
public class SectionDeltaFunction extends AbstractDeltaFunction<List<ConcentrationDelta>> {

    /**
     * The function.
     */
    private final Function<ConcentrationContainer, List<ConcentrationDelta>> function;

    /**
     * Creates a new section delta function with the specified condition and function.
     * @param function The function.
     * @param condition The condition.
     */
    public SectionDeltaFunction(Function<ConcentrationContainer, List<ConcentrationDelta>> function, Predicate<ConcentrationContainer> condition) {
        super(condition);
        this.function = function;
    }

    /**
     * Returns the function.
     * @return The function.
     */
    public Function<ConcentrationContainer, List<ConcentrationDelta>> getFunction() {
        return function;
    }

}
