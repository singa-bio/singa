package bio.singa.simulation.model.simulation.error;

import bio.singa.core.events.UpdateEventEmitter;
import bio.singa.core.events.UpdateEventListener;
import bio.singa.features.formatter.TimeFormatter;
import bio.singa.features.units.UnitRegistry;
import bio.singa.simulation.model.simulation.UpdateScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.units.indriya.ComparableQuantity;
import tech.units.indriya.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.quantity.Time;
import java.util.ArrayList;
import java.util.List;

import static bio.singa.simulation.model.simulation.error.ErrorManager.Reason;
import static bio.singa.simulation.model.simulation.error.ErrorManager.Reason.INCREASE;

/**
 * @author cl
 */
public class TimeStepManager implements UpdateEventEmitter<Reason> {

    private static final Logger logger = LoggerFactory.getLogger(TimeStepManager.class);
    private static TimeStepManager instance;

    private UpdateScheduler scheduler;
    private List<UpdateEventListener<Reason>> listeners;

    /**
     * The currently elapsed time.
     */
    private ComparableQuantity<Time> elapsedTime;

    private boolean timeStepRescaled;
    private long timeStepsDecreased = 0;
    private long timeStepsIncreased = 0;

    public TimeStepManager(UpdateScheduler scheduler) {
        this.scheduler = scheduler;
        elapsedTime = Quantities.getQuantity(0.0, UnitRegistry.getTimeUnit());
        listeners = new ArrayList<>();
    }

    private static TimeStepManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("Time step manager was not initialized");
        }
        return instance;
    }

    public static void initialize(UpdateScheduler scheduler) {
        synchronized (TimeStepManager.class) {
            instance = new TimeStepManager(scheduler);
        }
    }

    public static void updateTime() {
        getInstance().elapsedTime = getInstance().elapsedTime.add(UnitRegistry.getTime());
    }

    public static ComparableQuantity<Time> getElapsedTime() {
        return getInstance().elapsedTime;
    }

    public static void increaseTimeStep() {
        getInstance().increase();
    }

    private synchronized void increase() {
        // change timestep in accordance to error
        Quantity<Time> original = UnitRegistry.getTime();
        double multiplier = estimateIncrease();
        Quantity<Time> estimate = original.multiply(multiplier);
        if (scheduler.getSimulation().isDebug()) {
            scheduler.getSimulation().getDebugRecorder().addInformation(scheduler.getSimulation().getEpoch(), String.format("increasing time step %s -> %s", TimeFormatter.formatTime(original), TimeFormatter.formatTime(estimate)));
        }
        UnitRegistry.setTime(estimate);
        timeStepsIncreased++;
        emitEvent(INCREASE);
    }

    public static void decreaseTimeStep(Reason reason) {
        getInstance().decrease(reason);
    }

    private synchronized void decrease(Reason reason) {
        // if time step is rescaled for the very fist time this epoch remember the initial error and time step
        Quantity<Time> original = UnitRegistry.getTime();
        double multiplier = estimateDecrease();
        Quantity<Time> estimate = original.multiply(multiplier);
        if (scheduler.getSimulation().isDebug()) {
            scheduler.getSimulation().getDebugRecorder().addInformation(scheduler.getSimulation().getEpoch(), String.format("decreasing time step (%s, %s) %s -> %s", reason, scheduler.getErrorManager().getGlobalNumericalError(), TimeFormatter.formatTime(original), TimeFormatter.formatTime(estimate)));
        }
        UnitRegistry.setTime(estimate);
        timeStepsDecreased++;
        timeStepRescaled = true;
        scheduler.getSimulation().setEpochWithRescaledTimeStep(scheduler.getSimulation().getEpoch());
        emitEvent(reason);
    }

    private double estimateIncrease() {
//        double upperLimit = 1.6;
//        double lowerLimit = 1.1;
//        double estimate = recalculationCutoff / getLargestLocalError().getValue();
//        return Math.max(Math.min(upperLimit, estimate), lowerLimit);
        return 1.2;
    }

    private double estimateDecrease() {
//        double lowerLimit = 0.6;
//        double estimate = recalculationCutoff / getLargestLocalError().getValue();
//        return Math.max(lowerLimit, estimate);
        return 0.8;
    }

    @Override
    public List<UpdateEventListener<Reason>> getListeners() {
        return listeners;
    }

    public static void addListener(UpdateEventListener<Reason> listener) {
        getInstance().addEventListener(listener);
    }

    public static void setTimeStepRescaled(boolean timeStepRescaled) {
        getInstance().timeStepRescaled = timeStepRescaled;
    }

    public static boolean isTimeStepRescaled() {
        return getInstance().timeStepRescaled;
    }

    public static long getTimeStepsDecreased() {
        return getInstance().timeStepsDecreased;
    }

    public static long getTimeStepsIncreased() {
        return getInstance().timeStepsIncreased;
    }

}
