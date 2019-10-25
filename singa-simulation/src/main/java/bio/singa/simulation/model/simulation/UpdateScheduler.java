package bio.singa.simulation.model.simulation;

import bio.singa.features.formatter.TimeFormatter;
import bio.singa.features.quantities.MolarConcentration;
import bio.singa.features.units.UnitRegistry;
import bio.singa.simulation.model.modules.UpdateModule;
import bio.singa.simulation.model.modules.concentration.NumericalError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.units.indriya.AbstractUnit;
import tech.units.indriya.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.quantity.Frequency;
import javax.measure.quantity.Time;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

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

    private Iterator<UpdateModule> moduleIterator;
    private double recalculationCutoff = DEFAULT_RECALCULATION_CUTOFF;

    private boolean timeStepRescaled;
    private boolean timeStepAlteredInThisEpoch;
    private long timestepsDecreased = 0;
    private long timestepsIncreased = 0;
    private NumericalError largestLocalError;
    private UpdateModule localErrorModule;
    private double localErrorUpdate;

    private NumericalError largestGlobalError;

    private CountDownLatch countDownLatch;

    private final Deque<UpdateModule> modules;

    private double previousError;
    private Quantity<Time> previousTimeStep;
    private Quantity<Frequency> accuracyGain;

    private volatile boolean interrupted;
    private boolean globalErrorAcceptable;
    private boolean calculateGlobalError;
    private ThreadPoolExecutor executor;
    private final double moleculeFraction;

    public UpdateScheduler(Simulation simulation) {
        this.simulation = simulation;
        modules = new ArrayDeque<>(simulation.getModules());
        largestLocalError = NumericalError.MINIMAL_EMPTY_ERROR;
        largestGlobalError = NumericalError.MINIMAL_EMPTY_ERROR;
        moleculeFraction = MolarConcentration.moleculesToConcentration(1.0/10000.0);
    }

    public void initializeThreadPool() {
        if (modules.isEmpty()) {
            return;
        }
        executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(modules.size());
    }

    public double getRecalculationCutoff() {
        return recalculationCutoff;
    }

    public void setRecalculationCutoff(double recalculationCutoff) {
        this.recalculationCutoff = recalculationCutoff;
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
        timeStepAlteredInThisEpoch = false;
        previousError = 0;
        largestLocalError = NumericalError.MINIMAL_EMPTY_ERROR;
        previousTimeStep = UnitRegistry.getTime();
        updatables = simulation.getUpdatables();
        moduleIterator = modules.iterator();
        globalErrorAcceptable = true;
        calculateGlobalError = true;

        for (Updatable updatable : updatables) {
            updatable.getConcentrationManager().backupConcentrations();
        }

        // until all models passed
        do {
            if (timeStepRescaled || interrupted || !globalErrorAcceptable) {
                largestLocalError = NumericalError.MINIMAL_EMPTY_ERROR;
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
                evaluateGlobalNumericalAccuracy();
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

        // wrap up
        determineAccuracyGain();

        finalizeDeltas();
        modules.forEach(UpdateModule::reset);
    }

    public void evaluateGlobalNumericalAccuracy() {
        if (calculateGlobalError) {
            // calculate half step concentrations for subsequent evaluation
            // for each node
            for (Updatable updatable : updatables) {
                // calculate interim container (added current updates with checked local error)
                // set half step concentrations y(t+1/2dt) for interim containers
                // backup current concentrations and set current concentration to interim concentrations
                updatable.getConcentrationManager().setInterimAndUpdateCurrentConcentrations();
            }
            globalErrorAcceptable = false;
            calculateGlobalError = false;
        } else {
            // evaluate global numerical accuracy
            // for each node
            NumericalError largestGlobalError = NumericalError.MINIMAL_EMPTY_ERROR;
            for (Updatable updatable : updatables) {
                // determine full concentrations with full update and 2 * half update
                updatable.getConcentrationManager().determineComparisionConcentrations();
                // determine error between both
                NumericalError globalError = updatable.getConcentrationManager().determineGlobalNumericalError();
                if (largestGlobalError.isSmallerThan(globalError)) {
                    globalError.setUpdatable(updatable);
                    largestGlobalError = globalError;
                }
            }
            // set interim check false if global error is to large and true if you can continue
            if (largestGlobalError.getValue() > recalculationCutoff) {
                // System.out.println("rejected global error: "+largestGlobalError+" @ "+TimeFormatter.formatTime(UnitRegistry.getTime()));
                simulation.getScheduler().decreaseTimeStep();
                globalErrorAcceptable = false;
                calculateGlobalError = true;
            } else {
                // System.out.println("accepted global error: "+largestGlobalError+ " @ "+TimeFormatter.formatTime(UnitRegistry.getTime()));
                globalErrorAcceptable = true;
            }
            if (getLargestLocalError().getValue() != NumericalError.MINIMAL_EMPTY_ERROR.getValue()) {
                logger.debug("Largest local error : {} ({}, {}, {})", getLargestLocalError().getValue(), getLargestLocalError().getChemicalEntity(), getLargestLocalError().getUpdatable().getStringIdentifier(), getLocalErrorModule());
            } else {
                logger.debug("Largest local error : minimal");
            }
            logger.debug("Largest global error: {} ({}, {})", largestGlobalError.getValue(), largestGlobalError.getChemicalEntity(), largestGlobalError.getUpdatable().getStringIdentifier());
            this.largestGlobalError = largestGlobalError;
        }

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
        return timeStepRescaled || interrupted || !globalErrorAcceptable;
    }

    public NumericalError getLargestLocalError() {
        return largestLocalError;
    }

    public void setLargestLocalError(NumericalError localError, UpdateModule associatedModule, double associatedConcentration) {
        if (localError.getValue() > largestLocalError.getValue()) {
            largestLocalError = localError;
            localErrorModule = associatedModule;
            localErrorUpdate = associatedConcentration;
        }
    }

    public UpdateModule getLocalErrorModule() {
        return localErrorModule;
    }

    public double getLocalErrorUpdate() {
        return MolarConcentration.concentrationToMolecules(localErrorUpdate).getValue().doubleValue();
    }

    public NumericalError getLargestGlobalError() {
        return largestGlobalError;
    }

    public boolean timeStepWasRescaled() {
        return timeStepRescaled;
    }

    public void shutdownExecutorService() {
        executor.shutdown();
    }

    public boolean timeStepWasAlteredInThisEpoch() {
        return timeStepAlteredInThisEpoch;
    }

    public Quantity<Frequency> getAccuracyGain() {
        return accuracyGain;
    }

    public void increaseTimeStep() {
        UnitRegistry.setTime(UnitRegistry.getTime().multiply(1.1));
        logger.debug("Increasing time step to {}.", TimeFormatter.formatTime(UnitRegistry.getTime()));
        timestepsIncreased++;
    }

    public synchronized void decreaseTimeStep() {
        // if time step is rescaled for the very fist time this epoch remember the initial error and time step
        if (!timeStepWasAlteredInThisEpoch()) {
            previousError = largestLocalError.getValue();
            previousTimeStep = UnitRegistry.getTime();
        }
        UnitRegistry.setTime(UnitRegistry.getTime().multiply(0.9));
        logger.debug("Decreasing time step to {}.", TimeFormatter.formatTime(UnitRegistry.getTime()));
        timestepsDecreased++;
        timeStepRescaled = true;
        timeStepAlteredInThisEpoch = true;
    }

    private void finalizeDeltas() {
        for (Updatable updatable : updatables) {
            updatable.getConcentrationManager().shiftDeltas();
        }
        // potential updates for vesicles are already set during displacement evaluation
    }

    private void determineAccuracyGain() {
        if (timeStepWasAlteredInThisEpoch()) {
            final double errorDelta = previousError - largestLocalError.getValue();
            final Quantity<Time> timeDelta = previousTimeStep.subtract(UnitRegistry.getTime());
            accuracyGain = Quantities.getQuantity(errorDelta, AbstractUnit.ONE).divide(timeDelta).asType(Frequency.class);
        }
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
        // rest vesicle position
        simulation.getVesicleLayer().clearUpdates();
        if (calculateGlobalError) {
            updatables.forEach(updatable -> updatable.getConcentrationManager().revertToOriginalConcentrations());
        }
        // start from the beginning
        moduleIterator = modules.iterator();
    }

    private void evaluateSpatialDisplacement() {
        if (simulation.getVesicleLayer().getVesicles().isEmpty()) {
            return;
        }
        if (!simulation.getVesicleLayer().deltasAreBelowDisplacementCutoff()) {
            decreaseTimeStep();
            simulation.getVesicleLayer().clearUpdates();
            modules.forEach(UpdateModule::reset);
        }
    }

}
