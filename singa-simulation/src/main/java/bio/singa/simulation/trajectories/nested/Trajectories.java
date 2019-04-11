package bio.singa.simulation.trajectories.nested;

import bio.singa.features.quantities.MolarConcentration;

import javax.measure.Unit;
import javax.measure.quantity.Time;
import java.util.HashMap;
import java.util.Map;

/**
 * @author cl
 */
public class Trajectories {

    private Unit<Time> timeUnit;
    private Unit<MolarConcentration> concentrationUnit;
    private Map<Double, TrajectoryData> trajectoryData;

    public Trajectories() {
        trajectoryData = new HashMap<>();
    }

    public Trajectories(Unit<Time> timeUnit, Unit<MolarConcentration> concentrationUnit) {
        this();
        this.timeUnit = timeUnit;
        this.concentrationUnit = concentrationUnit;
    }

    public Unit<Time> getTimeUnit() {
        return timeUnit;
    }

    public Unit<MolarConcentration> getConcentrationUnit() {
        return concentrationUnit;
    }

    public Map<Double, TrajectoryData> getTrajectoryData() {
        return trajectoryData;
    }

    public void addTrajectoryData(Double timeStep, TrajectoryData data) {
        trajectoryData.put(timeStep, data);
    }

}
