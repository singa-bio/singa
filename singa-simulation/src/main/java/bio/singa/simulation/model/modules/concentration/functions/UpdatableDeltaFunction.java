package bio.singa.simulation.model.modules.concentration.functions;

import bio.singa.simulation.model.modules.concentration.ConcentrationBasedModule;
import bio.singa.simulation.model.modules.concentration.ConcentrationDelta;
import bio.singa.simulation.model.modules.concentration.ConcentrationDeltaIdentifier;
import bio.singa.simulation.model.modules.concentration.imlementations.reactions.Reaction;
import bio.singa.simulation.model.modules.concentration.specifity.UpdatableSpecific;
import bio.singa.simulation.model.sections.ConcentrationContainer;
import bio.singa.simulation.model.simulation.Updatable;

import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Updatable delta functions are {@link AbstractDeltaFunction}s which return a map with
 * {@link ConcentrationDeltaIdentifier}s mapping to to concentration deltas. Mostly used for {@link UpdatableSpecific}
 * {@link ConcentrationBasedModule}s, where the function is applied once per  {@link Updatable}. Each calculation
 * returns the change of a specified set of entities that ma span across sections or even updatables
 * (e.g. for {@link Reaction}s).
 *
 * @author cl
 */
public class UpdatableDeltaFunction extends AbstractDeltaFunction<Map<ConcentrationDeltaIdentifier, ConcentrationDelta>> {

    /**
     * The function.
     */
    private final Function<ConcentrationContainer, Map<ConcentrationDeltaIdentifier, ConcentrationDelta>> function;

    /**
     * Creates a new updatable delta function with the specified condition and function.
     * @param function The function.
     * @param condition The condition.
     */
    public UpdatableDeltaFunction(Function<ConcentrationContainer, Map<ConcentrationDeltaIdentifier, ConcentrationDelta>> function, Predicate<ConcentrationContainer> condition) {
        super(condition);
        this.function = function;
    }

    /**
     * Returns the function.
     * @return The function.
     */
    public Function<ConcentrationContainer, Map<ConcentrationDeltaIdentifier, ConcentrationDelta>> getFunction() {
        return function;
    }

}
