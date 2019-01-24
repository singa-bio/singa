package bio.singa.simulation.model.simulation;

import bio.singa.features.units.UnitRegistry;
import bio.singa.simulation.model.modules.UpdateModule;
import bio.singa.simulation.model.modules.concentration.LocalError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.CountDownLatch;

import static bio.singa.simulation.model.modules.concentration.ModuleState.SUCCEEDED_WITH_PENDING_CHANGES;

/**
 * @author cl
 */
public class UpdateScheduler {

    private static final Logger logger = LoggerFactory.getLogger(UpdateScheduler.class);

    /**
     * The default value where errors are considered too large and the time step is reduced.
     */
    private static final double DEFAULT_RECALCULATION_CUTOFF = 0.01;

    private Simulation simulation;
    private List<Updatable> updatables;

    private List<UpdateModule> modules;
    private ListIterator<UpdateModule> moduleIterator;
    private UpdateModule module;
    private double recalculationCutoff = DEFAULT_RECALCULATION_CUTOFF;

    private boolean timestepRescaled;
    private LocalError largestError;

    private CountDownLatch countDownLatch;

    public UpdateScheduler(Simulation simulation) {
        this.simulation = simulation;
        modules = simulation.getModules();
        largestError = LocalError.MINIMAL_EMPTY_ERROR;
    }

    public double getRecalculationCutoff() {
        return recalculationCutoff;
    }

    public void setRecalculationCutoff(double recalculationCutoff) {
        this.recalculationCutoff = recalculationCutoff;
    }

    public void nextEpoch() {
        // initialize fields
        simulation.collectUpdatables();
        updatables = simulation.getUpdatables();
        moduleIterator = modules.listIterator();

        // until all models passed
        do {
            if (timestepRescaled) {
                resetCalculation();
            }
            timestepRescaled = false;

            countDownLatch = new CountDownLatch(getNumberOfModules());
            logger.debug("Starting with latch at {}.", countDownLatch.getCount());

            int i = 0;
            while (moduleIterator.hasNext()) {
                i++;
                module = moduleIterator.next();
                Thread thread = new Thread(module, "Module " + i);
                thread.start();
            }

            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // evaluate total spatial displacement
            spatialDisplacementIsValid();

        } while (timestepRescaled);

        // resolve pending changes
        for (UpdateModule updateModule : modules) {
            if (updateModule.getState().equals(SUCCEEDED_WITH_PENDING_CHANGES)) {
                updateModule.onCompletion();
            }
        }

        logger.debug("Finished processing modules for epoch {}.", simulation.getEpoch());
        // wrap up
        finalizeDeltas();
        modules.forEach(UpdateModule::resetState);
    }

    public LocalError getLargestError() {
        return largestError;
    }

    public boolean timeStepWasRescaled() {
        return timestepRescaled;
    }

    public void increaseTimeStep() {
        UnitRegistry.setTime(UnitRegistry.getTime().multiply(1.2));
        logger.debug("Increasing time step to {}.", UnitRegistry.getTime());
        timestepRescaled = true;
    }

    public void decreaseTimeStep() {
        UnitRegistry.setTime(UnitRegistry.getTime().multiply(0.8));
        logger.debug("Decreasing time step to {}.", UnitRegistry.getTime());
        timestepRescaled = true;
    }

    private void finalizeDeltas() {
        for (Updatable updatable : updatables) {
            updatable.shiftDeltas();
        }
        // potential updates for vesicles are already set during displacement evaluation
    }

    public CountDownLatch getCountDownLatch() {
        return countDownLatch;
    }

    public int getNumberOfModules() {
        return modules.size();
    }

    public void resetCalculation() {
        logger.debug("Resetting calculations.");
        // reset states
        modules.stream()
                .filter(m -> m != module)
                .forEach(UpdateModule::resetState);
        // clear deltas that have previously been calculated
        updatables.forEach(updatable -> updatable.clearPotentialDeltasBut(module));
        // start from the beginning
        moduleIterator = modules.listIterator();
        // start from the beginning
        moduleIterator = modules.listIterator();
    }

    private boolean spatialDisplacementIsValid() {
        if (simulation.getVesicleLayer().getVesicles().isEmpty()) {
            return true;
        }
        if (!simulation.getVesicleLayer().deltasAreBelowDisplacementCutoff()) {
            decreaseTimeStep();
            simulation.getVesicleLayer().clearUpdates();
            modules.forEach(UpdateModule::resetState);
            return false;
        }
        return true;
    }

}
