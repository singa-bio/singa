package bio.singa.simulation.model.concentrations;

import bio.singa.simulation.model.simulation.Updatable;

/**
 * @author cl
 */
public interface ConcentrationCondition {

    int getPriority();
    boolean test(Updatable updatable);

}
