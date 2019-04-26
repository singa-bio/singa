package bio.singa.simulation.model.modules.concentration.functions;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.simulation.model.modules.concentration.ConcentrationBasedModule;
import bio.singa.simulation.model.modules.concentration.ConcentrationDelta;
import bio.singa.simulation.model.modules.concentration.imlementations.transport.Diffusion;
import bio.singa.simulation.model.modules.concentration.specifity.EntitySpecific;
import bio.singa.simulation.model.sections.CellSubsection;
import bio.singa.simulation.model.sections.ConcentrationContainer;
import bio.singa.simulation.model.simulation.Updatable;

import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Entity delta functions are {@link AbstractDeltaFunction}s which return only one concentration delta.
 * Mostly used for {@link EntitySpecific} {@link ConcentrationBasedModule}s, where the function is applied to every
 * {@link ChemicalEntity} in every {@link CellSubsection} and every {@link Updatable}. Each calculation returns
 * the change of the specific entity (e.g. for {@link Diffusion}).
 *
 * @author cl
 */
public class EntityDeltaFunction extends AbstractDeltaFunction<ConcentrationDelta> {

    /**
     * The function.
     */
    private final Function<ConcentrationContainer, ConcentrationDelta> function;

    /**
     * Creates a new entity delta function with the specified condition and function.
     * @param function The function.
     * @param condition The condition.
     */
    public EntityDeltaFunction(Function<ConcentrationContainer, ConcentrationDelta> function, Predicate<ConcentrationContainer> condition) {
        super(condition);
        this.function = function;
    }

    /**
     * Returns the function.
     * @return The function.
     */
    public Function<ConcentrationContainer, ConcentrationDelta> getFunction() {
        return function;
    }

}
