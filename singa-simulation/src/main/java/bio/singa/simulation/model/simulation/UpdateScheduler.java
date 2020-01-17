package bio.singa.simulation.model.simulation;

import bio.singa.features.formatter.TimeFormatter;
import bio.singa.features.quantities.MolarConcentration;
import bio.singa.features.units.UnitRegistry;
import bio.singa.simulation.model.modules.UpdateModule;
import bio.singa.simulation.model.simulation.error.DisplacementDeviation;
import bio.singa.simulation.model.simulation.error.ErrorManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.measure.Quantity;
import javax.measure.quantity.Time;
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

    private Simulation simulation;
    private List<Updatable> updatables;
    private Iterator<UpdateModule> moduleIterator;

    private boolean timeStepRescaled;

    private long timeStepsDecreased = 0;
    private long timeStepsIncreased = 0;

    private ErrorManager errorManager;

    private CountDownLatch countDownLatch;

    private volatile boolean interrupted;

    private ThreadPoolExecutor executor;

    public UpdateScheduler(Simulation simulation) {
        this.simulation = simulation;
        modules = new ArrayDeque<>(simulation.getModules());
        errorManager = new ErrorManager(this);
        moleculeFraction = MolarConcentration.moleculesToConcentration(1.0 / 50000.0);
    }

    public void initializeThreadPool() {
        if (modules.isEmpty()) {
            return;
        }
        executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(modules.size());
    }

    public long getTimeStepsDecreased() {
        return timeStepsDecreased;
    }

    public long getTimeStepsIncreased() {
        return timeStepsIncreased;
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

            timeStepRescaled = false;
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
            decreaseTimeStep("total displacement exceeded E(%6.3e)");
            simulation.getVesicleLayer().clearUpdates();
            modules.forEach(UpdateModule::reset);
        }

        if (timeStepRescaled) {
            errorManager.resetLocalDisplacementDeviation();
            simulation.getVesicleLayer().clearUpdates();
            recalculationRequired = true;
        } else {
            errorManager.evaluateGlobalError();
            if (!errorManager.globalErrorIsAcceptable()) {
                recalculationRequired = true;
            }
        }

        if (recalculationRequired) {
            logger.debug("Resetting calculations.");
            // reset states
            for (UpdateModule module : modules) {
                // skip modules with pending changes if time step was not rescaled
                if (module.getState().equals(SUCCEEDED_WITH_PENDING_CHANGES) && !timeStepRescaled) {
                    continue;
                }
                module.reset();
            }
            // clear deltas that have previously been calculated
            updatables.forEach(updatable -> updatable.getConcentrationManager().clearPotentialDeltas());
            if (errorManager.globalErrorIsAcceptable()) {
                updatables.forEach(updatable -> updatable.getConcentrationManager().revertToOriginalConcentrations());
            }
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

    public void increaseTimeStep(String reason) {
        // change timestep in accordance to error
        Quantity<Time> original = UnitRegistry.getTime();
        double multiplier = estimateIncrease();
        Quantity<Time> estimate = original.multiply(multiplier);

        if (simulation.isDebug()) {
            simulation.getDebugRecorder().addInformation(simulation.getEpoch(), String.format("increasing time step %s -> %s %s", TimeFormatter.formatTime(original), TimeFormatter.formatTime(estimate), reason));
        }

        UnitRegistry.setTime(estimate);

        timeStepsIncreased++;
    }

    public synchronized void decreaseTimeStep(String reason) {
        // if time step is rescaled for the very fist time this epoch remember the initial error and time step
        Quantity<Time> original = UnitRegistry.getTime();
        double multiplier = estimateDecrease();
        Quantity<Time> estimate = original.multiply(multiplier);

        if (simulation.isDebug()) {
            simulation.getDebugRecorder().addInformation(simulation.getEpoch(), String.format("decreasing time step %s -> %s %s", TimeFormatter.formatTime(original), TimeFormatter.formatTime(estimate), reason));
        }

        UnitRegistry.setTime(estimate);

        timeStepsDecreased++;
        timeStepRescaled = true;
    }

    public synchronized void decreaseTimeStep(DisplacementDeviation deviation) {
        // if time step is rescaled for the very fist time this epoch remember the initial error and time step
        Quantity<Time> original = UnitRegistry.getTime();
        double multiplier = estimateDecrease();
        Quantity<Time> estimate = original.multiply(multiplier);

        if (simulation.isDebug()) {
            simulation.getDebugRecorder().addInformation(simulation.getEpoch(), String.format("decreasing time step %s -> %s %s", TimeFormatter.formatTime(original), TimeFormatter.formatTime(estimate)));
        }

        UnitRegistry.setTime(estimate);

        timeStepsDecreased++;
        timeStepRescaled = true;
    }

    private double estimateIncrease() {
//        double upperLimit = 1.6;
//        double lowerLimit = 1.1;
//        double estimate = recalculationCutoff / getLargestLocalError().getValue();
//        return Math.max(Math.min(upperLimit, estimate), lowerLimit);
        return 1.3;
    }

    private double estimateDecrease() {
//        double lowerLimit = 0.6;
//        double estimate = recalculationCutoff / getLargestLocalError().getValue();
//        return Math.max(lowerLimit, estimate);
        return 0.7;
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

    public List<Updatable> getUpdatables() {
        return updatables;
    }
}
