package bio.singa.simulation.model.modules.concentration;

import bio.singa.simulation.model.simulation.UpdateScheduler;

/**
 * The state of the module is used to schedule the processing of modules in the {@link UpdateScheduler}.
 *
 * @author cl
 */
public enum ModuleState {

    /**
     * Currently unprocessed module.
     */
    PENDING,

    /**
     * Calculation has completed with numerical error below the given threshold. Updates are not yet applied.
     */
    SUCCEEDED,

    /**
     * The module has completed successfully but contains some changes to the simulation that should be applied at the
     * very end of the time step.
     */
    SUCCEEDED_WITH_PENDING_CHANGES,

    /**
     * The calculation has finished but the numerical error was too large.
     */
    REQUIRING_RECALCULATION,

    /**
     * Interrupted by another calculation
     */
    INTERRUPTED,

    /**
     * Some error has occurred during calculation.
     */
    ERRORED;

}
