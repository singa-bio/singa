package de.bioforscher.singa.simulation.model.modules.concentration.specifity;

import de.bioforscher.singa.simulation.model.modules.concentration.functions.AbstractDeltaFunction;
import de.bioforscher.singa.simulation.model.sections.ConcentrationContainer;

/**
 * @author cl
 */
public interface UpdateSpecificity<DeltaFunctionType extends AbstractDeltaFunction> {

    void processContainer(ConcentrationContainer container);

    void determineDeltas(ConcentrationContainer container);

    void addDeltaFunction(DeltaFunctionType deltaFunctionType);

}
