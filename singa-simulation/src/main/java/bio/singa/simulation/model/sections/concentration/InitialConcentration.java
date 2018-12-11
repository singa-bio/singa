package bio.singa.simulation.model.sections.concentration;

import bio.singa.features.model.Evidence;
import bio.singa.simulation.model.simulation.Simulation;

/**
 * @author cl
 */
public interface InitialConcentration  {

    void initialize(Simulation simulation);

    Evidence getEvidence();

}
