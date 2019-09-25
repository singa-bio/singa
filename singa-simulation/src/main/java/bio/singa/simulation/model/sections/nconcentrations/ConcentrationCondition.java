package bio.singa.simulation.model.sections.nconcentrations;

import bio.singa.simulation.model.simulation.Updatable;

/**
 * @author cl
 */
public interface ConcentrationCondition {

    int getPriority();
    boolean test(Updatable updatable);

}
