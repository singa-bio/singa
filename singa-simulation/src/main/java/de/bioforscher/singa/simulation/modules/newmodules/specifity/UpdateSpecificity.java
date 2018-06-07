package de.bioforscher.singa.simulation.modules.newmodules.specifity;

import de.bioforscher.singa.simulation.model.newsections.ConcentrationContainer;
import de.bioforscher.singa.simulation.modules.newmodules.functions.AbstractDeltaFunction;

/**
 * @author cl
 */
public interface UpdateSpecificity<DeltaFunctionType extends AbstractDeltaFunction> {

    void processContainer(ConcentrationContainer container);

    void determineDeltas(ConcentrationContainer container);

    void addDeltaFunction(DeltaFunctionType deltaFunctionType);

}
