package bio.singa.simulation.model.simulation;

import bio.singa.features.formatter.TimeFormatter;
import bio.singa.features.quantities.MolarConcentration;
import bio.singa.features.units.UnitRegistry;
import bio.singa.simulation.model.modules.UpdateModule;
import bio.singa.simulation.model.simulation.error.DisplacementDeviation;
import bio.singa.simulation.model.simulation.error.ErrorManager;
import bio.singa.simulation.model.simulation.error.GlobalNumericalErrorManager;
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

    private long timestepsDecreased = 0;
    private long timestepsIncreased = 0;

    private ErrorManager errorManager;
    private GlobalNumericalErrorManager globalErrorManager;

    private CountDownLatch countDownLatch;

    private volatile boolean interrupted;

    private ThreadPoolExecutor executor;

    public UpdateScheduler(Simulation simulation) {
        this.simulation = simulation;
        modules = new ArrayDeque<>(simulation.getModules());
        errorManager = new ErrorManager();
        globalErrorManager = new GlobalNumericalErrorManager(this);
        moleculeFraction = MolarConcentration.moleculesToConcentration(1.0 / 50000.0);
    }

    public void initializeThreadPool() {
        if (modules.isEmpty()) {
            return;
        }
        executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(modules.size());
    }

    public long getTimestepsDecreased() {
        return timestepsDecreased;
    }

    public long getTimestepsIncreased() {
        return timestepsIncreased;
    }

    public double getMoleculeFraction() {
        return moleculeFraction;
    }

    public void nextEpoch() {
        // initialize fields
        errorManager.resetLocalNumericalError();
        errorManager.resetLocalDisplacementDeviation();
        updatables = simulation.getUpdatables();
        moduleIterator = modules.iterator();

        globalErrorManager.setGlobalErrorAcceptable(true);
        globalErrorManager.setCalculateGlobalError(true);

        for (Updatable updatable : updatables) {
            updatable.getConcentrationManager().backupConcentrations();
        }

        // until all models passed
        do {
            if (timeStepRescaled || interrupted || !globalErrorManager.isGlobalErrorAcceptable()) {
                errorManager.resetLocalNumericalError();
                if (timeStepRescaled) {
                    errorManager.resetLocalDisplacementDeviation();
                }
                resetCalculation();
            }
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

            if (!interrupted) {
                // perform only if every module passed individually
                // evaluate total concentration change
                globalErrorManager.evaluateGlobalNumericalAccuracy();
            }

            // evaluate total spatial displacement
            evaluateSpatialDisplacement();

        } while (recalculationRequired());
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



    /**
     * Recalculations are required if:
     * <ul>
     * <li>the time step was rescaled during this calculation</li>
     * <li>any module was interrupted during this calculation</li>
     * <li>the global error was larger than the recalculation cutoff</li>
     * </ul>
     *
     * @return true, if a recalculation is required
     */
    public boolean recalculationRequired() {
        return timeStepRescaled || interrupted || !globalErrorManager.isGlobalErrorAcceptable();
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

        timestepsIncreased++;
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

        timestepsDecreased++;
        timeStepRescaled = true;
    }

    public synchronized void decreaseTimestep(DisplacementDeviation deviation) {
        // if time step is rescaled for the very fist time this epoch remember the initial error and time step
        Quantity<Time> original = UnitRegistry.getTime();
        double multiplier = estimateDecrease();
        Quantity<Time> estimate = original.multiply(multiplier);

        if (simulation.isDebug()) {
            simulation.getDebugRecorder().addInformation(simulation.getEpoch(), String.format("decreasing time step %s -> %s %s", TimeFormatter.formatTime(original), TimeFormatter.formatTime(estimate)));
        }

        UnitRegistry.setTime(estimate);

        timestepsDecreased++;
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

    public void addModule(UpdateModule module) {
        modules.add(module);
    }

    public int getNumberOfModules() {
        return modules.size();
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

    public void resetCalculation() {
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
        // rest vesicle position, but only if time step changes
        if (timeStepRescaled) {
            simulation.getVesicleLayer().clearUpdates();
        }
        if (globalErrorManager.isGlobalErrorAcceptable()) {
            updatables.forEach(updatable -> updatable.getConcentrationManager().revertToOriginalConcentrations());
        }
        // start from the beginning
        moduleIterator = modules.iterator();
    }

    private void evaluateSpatialDisplacement() {
        if (simulation.getVesicleLayer().getVesicles().isEmpty()) {
            return;
        }
        DisplacementDeviation globalDeviation = simulation.getVesicleLayer().determineGlobalDeviation();
        if (globalDeviation.getValue() < 0) {
            decreaseTimeStep("total displacement exceeded E(%6.3e)");
            simulation.getVesicleLayer().clearUpdates();
            modules.forEach(UpdateModule::reset);
        }
    }

    public ErrorManager getErrorManager() {
        return errorManager;
    }

    public List<Updatable> getUpdatables() {
        return updatables;
    }
}
