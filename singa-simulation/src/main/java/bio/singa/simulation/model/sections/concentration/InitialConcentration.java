package bio.singa.simulation.model.sections.concentration;

import bio.singa.features.model.Evidence;
import bio.singa.simulation.model.sections.CellSubsection;
import bio.singa.simulation.model.sections.CellTopology;
import bio.singa.simulation.model.simulation.Simulation;
import bio.singa.simulation.model.simulation.Updatable;

/**
 * @author cl
 */
public interface InitialConcentration  {

    default void initialize(Simulation simulation) {
        for (Updatable updatable : simulation.getUpdatables()) {
            initialize(updatable);
        }
    }

    static boolean updatableContainsSubsection(Updatable updatable, CellSubsection subsection) {
        return updatable.getCellRegion().getSubsections().contains(subsection);
    }

    void initialize(Updatable updatable);

    void initializeUnchecked(Updatable updatable, CellTopology topology);

    Evidence getEvidence();

}
