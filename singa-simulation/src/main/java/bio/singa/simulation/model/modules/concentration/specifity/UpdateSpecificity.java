package bio.singa.simulation.model.modules.concentration.specifity;

import bio.singa.simulation.entities.ChemicalEntity;
import bio.singa.simulation.model.modules.concentration.functions.AbstractDeltaFunction;
import bio.singa.simulation.model.sections.CellSubsection;
import bio.singa.simulation.model.sections.ConcentrationContainer;

/**
 * Determines how fine-grained the updates need to be calculated. There are three levels of specificity:
 * <ul>
 *      <li> {@link EntitySpecific} - once for every chemical entity in every subsection of a updatable
 *      <li> {@link SectionSpecific} - once for every subsection of a updatable
 *      <li> {@link UpdatableSpecific} - once for every updatable
 * </ul>
 *
 * @author cl
 */
public interface UpdateSpecificity<DeltaFunctionType extends AbstractDeltaFunction> {

    /**
     * Processes a concentration container.
     * @param container The container.
     */
    void processContainer(ConcentrationContainer container);

    void processContainer(ConcentrationContainer container, CellSubsection subsection, ChemicalEntity chemicalEntity);

    /**
     * Determines all deltas for a Concentration container.
     * @param container The container.
     */
    void determineDeltas(ConcentrationContainer container);

    /**
     * Adds the implementation of a {@link AbstractDeltaFunction} to be calculated during simulation.
     * @param deltaFunctionType The delta function.
     */
    void addDeltaFunction(DeltaFunctionType deltaFunctionType);

}
