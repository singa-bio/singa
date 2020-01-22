package bio.singa.simulation.model.simulation.error;

import bio.singa.core.events.UpdateEventEmitter;
import bio.singa.core.events.UpdateEventListener;
import bio.singa.features.formatter.TimeFormatter;
import bio.singa.features.units.UnitRegistry;
import bio.singa.simulation.model.simulation.UpdateScheduler;

import javax.measure.Quantity;
import javax.measure.quantity.Time;
import java.util.ArrayList;
import java.util.List;

import static bio.singa.simulation.model.simulation.error.ErrorManager.*;
import static bio.singa.simulation.model.simulation.error.ErrorManager.Reason.*;

/**
 * @author cl
 */
public class TimeStepManager implements UpdateEventEmitter<Reason> {

    private UpdateScheduler scheduler;
    private List<UpdateEventListener<Reason>> listeners;

    private boolean timeStepRescaled;
    private long timeStepsDecreased = 0;
    private long timeStepsIncreased = 0;

    public TimeStepManager(UpdateScheduler scheduler) {
        this.scheduler = scheduler;
        listeners = new ArrayList<>();
    }

    public void increaseTimeStep() {
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

    public synchronized void decreaseTimeStep(Reason reason) {
        // if time step is rescaled for the very fist time this epoch remember the initial error and time step
        Quantity<Time> original = UnitRegistry.getTime();
        double multiplier = estimateDecrease();
        Quantity<Time> estimate = original.multiply(multiplier);

        if (scheduler.getSimulation().isDebug()) {
            scheduler.getSimulation().getDebugRecorder().addInformation(scheduler.getSimulation().getEpoch(), String.format("decreasing time step %s -> %s", TimeFormatter.formatTime(original), TimeFormatter.formatTime(estimate)));
        }

        UnitRegistry.setTime(estimate);

        timeStepsDecreased++;
        timeStepRescaled = true;
        emitEvent(reason);
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

    @Override
    public List<UpdateEventListener<Reason>> getListeners() {
        return listeners;
    }

    public void setTimeStepRescaled(boolean timeStepRescaled) {
        this.timeStepRescaled = timeStepRescaled;
    }

    public boolean isTimeStepRescaled() {
        return timeStepRescaled;
    }

    public long getTimeStepsDecreased() {
        return timeStepsDecreased;
    }

    public long getTimeStepsIncreased() {
        return timeStepsIncreased;
    }

}
