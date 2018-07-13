package de.bioforscher.singa.simulation.model.modules.concentration;

import de.bioforscher.singa.simulation.model.simulation.UpdateScheduler;

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
     * Calculation has completed with numerical error below the given threshold (set by
     * {@link UpdateScheduler#setRecalculationCutoff(double)}). Updates are not yet applied.
     */
    SUCCEEDED,

    /**
     * The calculation has finished but the numerical error was too large (set by
     * {@link UpdateScheduler#setRecalculationCutoff(double)}).
     */
    REQUIRING_RECALCULATION,

    /**
     * Some error has occurred during calculation.
     */
    ERRORED

}
