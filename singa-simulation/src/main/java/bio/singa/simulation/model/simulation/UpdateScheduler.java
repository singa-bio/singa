package bio.singa.simulation.model.simulation;

import bio.singa.features.quantities.MolarConcentration;
import bio.singa.simulation.model.modules.UpdateModule;
import bio.singa.simulation.model.simulation.error.ErrorManager;
import bio.singa.simulation.model.simulation.error.TimeStepManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import static bio.singa.simulation.model.modules.concentration.ModuleState.SUCCEEDED_WITH_PENDING_CHANGES;

/**
 * @author cl
 */
public class UpdateScheduler {

    private static final Logger logger = LoggerFactory.getLogger(UpdateScheduler.class);
    private final Deque<UpdateModule> modules;
    private final double moleculeFraction;
    private ErrorManager errorManager;
    private TimeStepManager timeStepManager;
    private Simulation simulation;
    private List<Updatable> updatables;
    private Iterator<UpdateModule> moduleIterator;


    private CountDownLatch countDownLatch;

    private volatile boolean interrupted;

    private ThreadPoolExecutor executor;

    public UpdateScheduler(Simulation simulation) {
        this.simulation = simulation;
        errorManager = new ErrorManager(this);
        timeStepManager = new TimeStepManager(this);
        modules = new ArrayDeque<>(simulation.getModules());
        moleculeFraction = MolarConcentration.moleculesToConcentration(1.0 / 50000.0);
    }

    public void initialize() {
        errorManager.initialize();
        if (modules.isEmpty()) {
            return;
        }
        executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(modules.size());
    }

    public double getMoleculeFraction() {
        return moleculeFraction;
    }

    public void nextEpoch() {
        // initialize
        errorManager.resetLocalNumericalError();
        errorManager.resetGlobalNumericalError();
        errorManager.resetLocalDisplacementDeviation();
        updatables = simulation.getUpdatables();
        moduleIterator = modules.iterator();

        for (Updatable updatable : updatables) {
            updatable.getConcentrationManager().backupConcentrations();
        }

        boolean recalculationRequired;
        // until all models passed
        do {

            timeStepManager.setTimeStepRescaled(false);
            interrupted = false;

            countDownLatch = new CountDownLatch(getNumberOfModules());
            logger.debug("Starting with latch at {}.", countDownLatch.getCount());

            int i = 0;
            while (moduleIterator.hasNext()) {
                i++;
                UpdateModule module = moduleIterator.next();
                // modules with pending changes generally only need to be calculated once if the time step was not reset (this is managed while resetting calculations)
                if (!interrupted && !module.getState().equals(SUCCEEDED_WITH_PENDING_CHANGES)) {
                    executor.execute(module);
                } else {
                    logger.debug("Skipping module {}, decreasing latch to {}.", i, countDownLatch.getCount());
                    countDownLatch.countDown();
                }
            }

            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            recalculationRequired = recalculationRequired();

        } while (recalculationRequired);
        // System.out.println("accepted local error: "+largestLocalError.getValue());
        // resolve pending changes
        for (UpdateModule updateModule : modules) {
            if (updateModule.getState().equals(SUCCEEDED_WITH_PENDING_CHANGES)) {
                updateModule.onCompletion();
            }
        }

        logger.debug("Finished processing modules for epoch {}.", simulation.getEpoch());

        finalizeDeltas();
        modules.forEach(UpdateModule::reset);
    }

    private void handleRecalculation() {

    }

    private boolean recalculationRequired() {
        boolean recalculationRequired = false;

        errorManager.evaluateGlobalDeviation();
        if (!errorManager.globalDeviationIsAcceptable()) {
            errorManager.resolveGlobalDeviationProblem();
            recalculationRequired = true;
        }

        errorManager.evaluateGlobalError();
        if (!errorManager.globalErrorIsAcceptable()) {
            errorManager.resolveGlobalErrorProblem();
            recalculationRequired = true;
        }

        if (timeStepManager.isTimeStepRescaled()) {
            errorManager.resetLocalDisplacementDeviation();
            simulation.getVesicleLayer().clearUpdates();
            modules.forEach(UpdateModule::reset);
            recalculationRequired = true;
        }

        if (recalculationRequired) {
            // reset states
            for (UpdateModule module : modules) {
                // skip modules with pending changes if time step was not rescaled
                if (module.getState().equals(SUCCEEDED_WITH_PENDING_CHANGES) && !timeStepManager.isTimeStepRescaled()) {
                    continue;
                }
                module.reset();
            }
            // clear deltas that have previously been calculated
            updatables.forEach(updatable -> updatable.getConcentrationManager().clearPotentialDeltas());
            // reset error
            errorManager.resetLocalNumericalError();
            // start from the beginning
            moduleIterator = modules.iterator();
        }

        return recalculationRequired;
    }

    public void shutdownExecutorService() {
        executor.shutdown();
    }

    private void finalizeDeltas() {
        for (Updatable updatable : updatables) {
            updatable.getConcentrationManager().shiftDeltas();
        }
        // potential updates for vesicles are already set during displacement evaluation
    }

    public CountDownLatch getCountDownLatch() {
        return countDownLatch;
    }

    public Deque<UpdateModule> getModules() {
        return modules;
    }

    public void addModule(UpdateModule module) {
        modules.add(module);
    }

    public int getNumberOfModules() {
        return modules.size();
    }

    public Simulation getSimulation() {
        return simulation;
    }

    /**
     * Returns true if the calling module was the first to call the method, therefore being allowed to optimize the
     * time step.
     *
     * @return True if the calling module was the first to call the method.
     */
    public synchronized boolean interrupt() {
        // interrupt all threads but the calling thread and count down their latch
        if (!interrupted) {
            interrupted = true;
            return true;
        }
        return false;
    }

    public ErrorManager getErrorManager() {
        return errorManager;
    }

    public TimeStepManager getTimeStepManager() {
        return timeStepManager;
    }

    public List<Updatable> getUpdatables() {
        return updatables;
    }
}
