package bio.singa.simulation.trajectories.nested;

import bio.singa.features.quantities.MolarConcentration;
import bio.singa.simulation.model.simulation.Updatable;

import javax.measure.Unit;
import java.util.*;

/**
 * @author cl
 */
public class TrajectoryData {

    private Map<Updatable, TrajectoryDataPoint> concentrationData;

    public TrajectoryData() {
        concentrationData = new HashMap<>();
    }

    public Map<Updatable, TrajectoryDataPoint> getConcentrationData() {
        return concentrationData;
    }

    public static TrajectoryData of(Collection<Updatable> updatables, Unit<MolarConcentration> concentrationUnit) {
        TrajectoryData data = new TrajectoryData();
        for (Updatable updatable : updatables) {
            data.put(updatable, TrajectoryDataPoint.of(updatable, concentrationUnit));
        }
        return data;
    }

    public void put(Updatable updatable, TrajectoryDataPoint dataPoint) {
        concentrationData.put(updatable, dataPoint);
    }

}
