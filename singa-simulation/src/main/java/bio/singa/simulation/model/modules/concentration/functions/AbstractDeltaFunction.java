package bio.singa.simulation.model.modules.concentration.functions;

import bio.singa.simulation.model.modules.concentration.ConcentrationDelta;
import bio.singa.simulation.model.sections.ConcentrationContainer;
import bio.singa.simulation.model.simulation.Updatable;

import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Delta Functions are used to calculate updates for {@link Updatable}s during simulation. Every delta function consists
 * of a condition and a function with a concentration container as input and varying return type. The condition is
 * evaluated before each function is applied and only if the result is true the function is evaluated. The function then
 * calculates the return value (mostly some {@link ConcentrationDelta}) as specified.
 *
 * @author cl
 */
public abstract class AbstractDeltaFunction<FunctionReturnType> {

    /**
     * The application condition.
     */
    private final Predicate<ConcentrationContainer> condition;

    /**
     * Creates a new DeltaFunction with the given application condition.
     * @param condition The application condition.
     */
    AbstractDeltaFunction(Predicate<ConcentrationContainer> condition) {
        this.condition = condition;
    }

    /**
     * Returns the application condition.
     * @return The application condition.
     */
    public Predicate<ConcentrationContainer> getCondition() {
        return condition;
    }

    /**
     * Returns the function.
     * @return The function.
     */
    abstract Function<ConcentrationContainer, FunctionReturnType> getFunction();

}
