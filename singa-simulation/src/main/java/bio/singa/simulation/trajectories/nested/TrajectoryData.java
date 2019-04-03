package bio.singa.simulation.trajectories.nested;

import bio.singa.features.quantities.MolarConcentration;
import bio.singa.simulation.model.simulation.Updatable;

import javax.measure.Unit;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author cl
 */
public class TrajectoryData {

    private Map<Updatable, TrajactoryDataPoint> concentrationData;

    private TrajectoryData() {
        concentrationData = new TreeMap<>(Comparator.comparing(Updatable::getStringIdentifier, String::compareTo));
    }

    public Map<Updatable, TrajactoryDataPoint> getConcentrationData() {
        return concentrationData;
    }

    public static TrajectoryData of(Collection<Updatable> updatables, Unit<MolarConcentration> concentrationUnit) {
        TrajectoryData data = new TrajectoryData();
        for (Updatable updatable : updatables) {
            data.concentrationData.put(updatable, TrajactoryDataPoint.of(updatable, concentrationUnit));
        }
        return data;
    }

}
