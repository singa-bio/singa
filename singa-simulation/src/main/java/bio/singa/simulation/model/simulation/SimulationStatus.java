package bio.singa.simulation.model.simulation;

import bio.singa.core.events.UpdateEventListener;
import bio.singa.features.formatter.TimeFormatter;
import bio.singa.features.units.UnitRegistry;
import bio.singa.simulation.events.GraphUpdatedEvent;
import tec.uom.se.ComparableQuantity;
import tec.uom.se.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.quantity.Time;

import static tec.uom.se.unit.MetricPrefix.MICRO;
import static tec.uom.se.unit.MetricPrefix.MILLI;
import static tec.uom.se.unit.Units.SECOND;

/**
 * @author cl
 */
public class SimulationStatus implements UpdateEventListener<GraphUpdatedEvent> {

    private Simulation simulation;

    private long previousEpochs;
    private long previousIncreases;
    private long previousDecreases;

    private long deltaEpochs;
    private long deltaIncreases;
    private long deltaDecreases;

    private long startingTime = System.currentTimeMillis();
    private Quantity<Time> terminationTime;
    private long previousTimeMillis = 0;
    private Quantity<Time> previousTimeSimulation = Quantities.getQuantity(0.0, UnitRegistry.getTimeUnit());

    private Quantity<Time> estimatedTimeRemaining;
    private Quantity<Time> estimatedSpeed;

    public SimulationStatus(Simulation simulation) {
        this.simulation = simulation;
    }

    public void setTerminationTime(Quantity<Time> terminationTime) {
        this.terminationTime = terminationTime;
    }

    public Quantity<Time> getTerminationTime() {
        return terminationTime;
    }

    @Override
    public void onEventReceived(GraphUpdatedEvent event) {
        calculateEpochBasedStatus();
        calculateTimeBasedStatus();
    }

    private void calculateEpochBasedStatus() {
        // update variables
        long currentEpochs = simulation.getEpoch();
        long currentIncreases = simulation.getScheduler().getTimestepsIncreased();
        long currentDecreases = simulation.getScheduler().getTimestepsDecreased();
        // determine change in epochs
        deltaEpochs = currentEpochs - previousEpochs;
        deltaIncreases = currentIncreases - previousIncreases;
        deltaDecreases = currentDecreases - previousDecreases;
        // update previous values
        previousEpochs = currentEpochs;
        previousIncreases = currentIncreases;
        previousDecreases = currentDecreases;
    }

    private void calculateTimeBasedStatus() {
        // calculate time remaining
        long currentTimeMillis = System.currentTimeMillis();
        ComparableQuantity<Time> currentTimeSimulation = simulation.getElapsedTime().to(MICRO(SECOND));
        double fractionDone = currentTimeSimulation.getValue().doubleValue() / terminationTime.getValue().doubleValue();
        long timeRequired = System.currentTimeMillis() - startingTime;
        long estimatedMillisRemaining = (long) (timeRequired / fractionDone) - timeRequired;
        estimatedTimeRemaining = Quantities.getQuantity(estimatedMillisRemaining, MILLI(SECOND));
        // calculate speed
        double speedInMicroSecondsPerSecond = currentTimeSimulation.subtract(previousTimeSimulation).getValue().doubleValue() / Quantities.getQuantity(currentTimeMillis - previousTimeMillis, MILLI(SECOND)).to(SECOND).getValue().doubleValue();
        estimatedSpeed = Quantities.getQuantity(speedInMicroSecondsPerSecond, MICRO(SECOND));
        // update previous values
        previousTimeMillis = currentTimeMillis;
        previousTimeSimulation = currentTimeSimulation;
    }

    public String getLargestLocalError() {
        return String.valueOf(simulation.getScheduler().getLargestLocalError().getValue());
    }

    public String getLargestLocalErrorUpdate() {
        return String.valueOf(simulation.getScheduler().getLocalErrorUpdate());
    }

    public String getLargestGlobalError() {
        return String.valueOf(simulation.getScheduler().getLargestGlobalError());
    }

    public String getNumberOfEpochsSinceLastUpdate() {
        return String.valueOf(deltaEpochs);
    }

    public String getNumberOfTimeStepIncreasesSinceLastUpdate() {
        return String.valueOf(deltaIncreases);
    }

    public String getNumberOfTimeStepDecreasesSinceLastUpdate() {
        return String.valueOf(deltaDecreases);
    }

    public long getProgressInMilliSeconds() {
        return simulation.getElapsedTime().to(MILLI(SECOND)).getValue().longValue();
    }

    public String getEstimatedTimeRemaining() {
        if (estimatedTimeRemaining != null) {
            return TimeFormatter.formatTime(estimatedTimeRemaining);
        }
        return "";
    }

    public String getEstimatedSpeed() {
        if (estimatedSpeed != null) {
            return TimeFormatter.formatTime(estimatedSpeed);
        }
        return "";
    }

    public String getElapsedTime() {
        return TimeFormatter.formatTime(simulation.getElapsedTime());
    }

    public String getAccuracyGain() {
        if (simulation.getScheduler().getAccuracyGain() != null) {
            return simulation.getScheduler().getAccuracyGain().toString();
        }
        return "";
    }

}
